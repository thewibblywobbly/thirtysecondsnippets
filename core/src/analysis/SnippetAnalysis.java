/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import audio.MP3Decoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author George
 */
public class SnippetAnalysis {
    
    private FileInputStream FILE = null;
    private final int HOP_SIZE = 512;
    private final int HISTORY_SIZE = 50;
    private final float[] multipliers = { 2f, 2f, 2f };
    private final float[] bands = { 80, 4000, 4000, 10000, 10000, 16000 };
    List<List<Float>> peaks;
    
    public SnippetAnalysis(){
        peaks = null;
    }
    
    public SnippetAnalysis(String file){
       try {
            this.FILE = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SnippetAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
       peaks = null;
    }
    
    public void setFile(String file){
        try {
            this.FILE = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SnippetAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<List<Float>> doAnalysis(String s){
        try {
            MP3Decoder decoder = new MP3Decoder(new FileInputStream( s  ));
            SpectrumProvider spectrumProvider = new SpectrumProvider( decoder, 1024, HOP_SIZE, true );
            float[] spectrum = spectrumProvider.nextSpectrum();
            float[] lastSpectrum = new float[spectrum.length];
            List<List<Float>> spectralFlux = new ArrayList<List<Float>>( );
            for( int i = 0; i < bands.length / 2; i++ )
                spectralFlux.add( new ArrayList<Float>( ) );
            
            do
            {
                for( int i = 0; i < bands.length; i+=2 )
                {
                    int startFreq = spectrumProvider.getFFT().freqToIndex( bands[i] );
                    int endFreq = spectrumProvider.getFFT().freqToIndex( bands[i+1] );
                    float flux = 0;
                    for( int j = startFreq; j <= endFreq; j++ )
                    {
                        float value = (spectrum[j] - lastSpectrum[j]);
                        value = (value + Math.abs(value))/2;
                        flux += value;
                    }
                    spectralFlux.get(i/2).add( flux );
                }
                
                System.arraycopy( spectrum, 0, lastSpectrum, 0, spectrum.length );
            }
            while( (spectrum = spectrumProvider.nextSpectrum() ) != null );
            
            List<List<Float>> thresholds = new ArrayList<List<Float>>( );
            for( int i = 0; i < bands.length / 2; i++ )
            {
                List<Float> threshold = new ThresholdFunction( HISTORY_SIZE, multipliers[i] ).calculate( spectralFlux.get(i) );
                thresholds.add( threshold );
            }
            
            List<List<Float>> prunnedSpectralFlux = new ArrayList<List<Float>>( );
            for( int i = 0; i < bands.length / 2; i++ ){
                prunnedSpectralFlux.add(new ArrayList<Float>());
                for( int j = 0; j < thresholds.get(i).size(); j++ )
                {
                    if( thresholds.get(i).get(j) <= spectralFlux.get(i).get(j) )
                        prunnedSpectralFlux.get(i).add( spectralFlux.get(i).get(j) - thresholds.get(i).get(j) );
                    else
                        prunnedSpectralFlux.get(i).add( (float)0 );
                }
            }
            
             peaks = new ArrayList<List<Float>>( );
            for(int i = 0;i<prunnedSpectralFlux.size();i++){
                peaks.add(new ArrayList<Float>());
                for(int j = 0;j<prunnedSpectralFlux.get(i).size()-1;j++){
                    if( prunnedSpectralFlux.get(i).get(j) > prunnedSpectralFlux.get(i).get(j+1) ){
                        peaks.get(i).add( prunnedSpectralFlux.get(i).get(j) );
                    }else{
                        peaks.get(i).add( (float)0 );
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SnippetAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return peaks;
    }
    
    public List<List<Float>> getPeaks(){
        return peaks;
    }
}