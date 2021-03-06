package com.gdp.thirtysecondsnippets;

import analysis.SnippetAnalysis;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThirtySecondSnippets implements InputProcessor, Screen {

    Preferences prefs = Gdx.app.getPreferences("30SSSettings");
    
    SpriteBatch batch;
    Texture threadlet, background, scissor1, scissor2, scissor3, scissor4, 
            scissor5, scissor6, redlet, shortthreadlet, threadedBackground, 
            colorBackground, shadowBackground, needleYellow, needleGreen, 
            needleBlue, star1, star2, star3, star4, star5, woodsBack, woodsFront,
            woodsBackground, woodsClouds, emptyTree, tail;
    Sprite player_sprite;
    World world;
    Body body;
    int iterator = 6, lastiterator = 5;
    int timer = 0, timerpaceClosed = 20, timerpaceOpen = 4;
    float posX, posY;
    float scissorsX, scissorsY;
    
    int runtimeCounter = 0;
    int backgroundType = 2;
    
    float width, height;
    int screen_top_height = 5;
    float bgx, bgcolorx, bgcloudx;
    long lastTimeBg;
    long lastTimeTempo;
    
    BitmapFont font,blackfont;
    CharSequence lbl_score = "Score: ";
    CharSequence score_amount = "";
    CharSequence multiplier = "";
    CharSequence bonus = "";
    CharSequence hyperthreading = "";
    CharSequence songTitle = "";
    CharSequence songArtist = "";
    CharSequence titleDisplay = "";
    
    int score = 0;
    int needle_combo = 0;
    int needle_hit = 1;
    static final int GROWTH_SUPRESSOR = 0;
    static final int SCORE_CONSTANT = 1;
    boolean HYPERTHREADING_MODE = false;
    
    int tempo;
    int lastRand = 0;
    int topSpacer = 0;
    int bottomSpacer = 0;
    
    int growthTimer = 0;
    
    int counter = 0;
    boolean growThread = false;
    boolean growableAllowed = true;
    boolean particlesAllowed = false;
    
    static final short THREAD_BIT = 2;
    static final short HEAD_BIT = 4;
    static final short BLADE_BIT = 8;
    static final short SCISSOR_BIT = 16;
    static final short NEEDLE_BIT = 32;
    static final short NEEDLE_HOLE_BIT = 64;
    static final short NO_COLLIDE_BIT = 128;
    static final short PARTICLE_BIT = 256;
    
    float BACKGROUND_SPEED = 28.8f;
    static final int STARTING_LENGTH = 2;
    static final int MAX_THREAD_LENGTH = 5;
    int SPACER_AMOUNT = 0;
    static final int SPAWN_RATE = 7;
    static final int GROWTH_TIMER_OFFSET = 4;
    
    static final int LEISURELY_DIFFICULTY = 7;
    static final int BRISK_DIFFICULTY = 5;
    static final int BREAKNECK_DIFFICULTY = 3;
    
    int difficulty = 0;
    
    float SCROLLING_FOREGROUND_SPEED = tempo/60f*-3f;
    
    ArrayList<JointEdge> jointDeletionList = new ArrayList<JointEdge>();
    boolean jointDestroyable = true;
    
    ArrayList<Sprite> threadSprites = new ArrayList<Sprite>();
    ArrayList<Body> threadBodies = new ArrayList<Body>();
    
    ArrayList<Sprite> scissorSprites = new ArrayList<Sprite>();
    ArrayList<Body> scissorBodies = new ArrayList<Body>();
    
    ArrayList<Sprite> needleSprites = new ArrayList<Sprite>();
    ArrayList<Body> needleBodies = new ArrayList<Body>();
    
    ArrayList<Vector2> queueToRemove = new ArrayList<Vector2>();
    
    ArrayList<Sprite> particleSprites = new ArrayList<Sprite>();
    ArrayList<Body> particleBodies = new ArrayList<Body>();
    
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    private Viewport viewport;
    Matrix4 debugMatrix;
    
    Vector2 mouseLoc;
    
    float torque = 0.0f;
    boolean drawSprite = true;
    boolean drawBoxes = false;
    boolean drawText = true;
    
    final float PIXELS_TO_METERS = 100f;
    
    boolean dansTryingToGetWorkDone = true;
    private Game tss;
    
    Music m = null;
    
    int beatIndex = 0;
    
    List<List<Float>> peaks = null;
    
    List<Float> spectralFluxBass = null;
    
    int lastDisplayed = 0;

    int displayInterval = (int) (0.1 / (512.0/44100.0)); 
    
    private Track track;
    
    private SnippetAnalysis analysis;
    
    ArrayList<RevoluteJoint> joints = new ArrayList<RevoluteJoint>();
    
    int needles_thread = 0;
    int thread_cut = 0;
    int beats = 0;
    int needles = 0;
    int scissors = 0;

    MusicDB db = new MusicDB();
    
    public ThirtySecondSnippets(Game tss){
        this.tss = tss;
        
        try {
                
                track = db.getTrackByGenre("rock");
                System.out.println(track.getArtist() + " | " + track.getName() + " | " + track.getTempo());
                songTitle = track.getName();
                songArtist = track.getArtist();
                
//                if (track.getGenreId() % 2 == 1){
//                    backgroundType = 1;
//                } else {
//                    backgroundType = 2;
//                }
                
                tempo = (int)track.getTempo();
                timerpaceClosed = tempo/60 * 40; 
                timerpaceOpen = tempo/60 * 12;
                
                String filename = "music.mp3";
                InputStream is = new URL(track.getPreview_url()).openStream();
                BufferedInputStream stream = new BufferedInputStream(is);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                FileHandle handle = Gdx.files.external(filename);
                if (handle.exists()) {
                    handle.delete();
                }
                int current = 0;
                while ((current = stream.read()) != -1) {
                    bytes.write(current);
                }
                FileOutputStream fos = new FileOutputStream(handle.file());
                bytes.writeTo(fos);

                m = Gdx.audio.newMusic(handle);
                System.out.println(bytes.size());
                
                analysis = new SnippetAnalysis(handle);
                peaks = analysis.doAnalysis();
                spectralFluxBass = analysis.getSpectralFluxBass();
            } catch (MalformedURLException ex) {
                Logger.getLogger(ThirtySecondSnippets.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ThirtySecondSnippets.class.getName()).log(Level.SEVERE, null, ex);
            } 
    }
    
    public ThirtySecondSnippets(Game tss, Track track, SnippetAnalysis analysis, Music m, int difficulty){
        this.tss = tss;
        this.track = track;
        this.analysis = analysis;
        this.m = m;
        this.difficulty = difficulty;
        SPACER_AMOUNT = difficulty;
        peaks = analysis.getPeaks();
        spectralFluxBass = analysis.getSpectralFluxBass();
        
        timerpaceClosed = tempo/60 * 40; 
        timerpaceOpen = tempo/60 * 12;
           
        System.out.println("Genre: " + track.getGenre());
        
        
        backgroundType = prefs.getInteger("theme", 1);

//        if ("blues".equals(track.getGenre())){
//            backgroundType = 4;
//        }else if ("jazz".equals(track.getGenre())){
//            backgroundType = 5;
//        }else if ("metal".equals(track.getGenre())){
//            backgroundType = 3;
//        } else if ("folk".equals(track.getGenre())){
//            backgroundType = 2;
//        } else {
//            backgroundType = 1;
//        }
        
        
        
        titleDisplay = songTitle = track.getName();
        songArtist = track.getArtist();
        tempo = (int)track.getTempo();
        //System.out.println("----------------");
        //System.out.println("Difficulty = " + difficulty);
        //System.out.println("----------------");    
    }
    
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Buttons.LEFT) {
            posX = screenX - player_sprite.getWidth();
            posY = Gdx.graphics.getHeight() - screenY - player_sprite.getHeight() / 2;
            mouseLoc = new Vector2(posX, posY);

            if (posY > (body.getPosition().y* PIXELS_TO_METERS)){
                body.applyForceToCenter(new Vector2(0f,2f), true);                
            } else {
                body.applyForceToCenter(new Vector2(0f,-2f), true);
            }
            body.setTransform(body.getPosition().x, posY/PIXELS_TO_METERS, 0);
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resume() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dispose() {
        
        threadlet.dispose();
        background.dispose();
        scissor1.dispose();
        world.dispose();

        System.out.println("Good day kind sir.");
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            torque += 0.01f;
        }
        if (keycode == Keys.RIGHT) {
            torque -= 0.01f;
        }
        if (keycode == Keys.UP) {
            torque = 0f;
        }
        if(keycode == Input.Keys.ESCAPE){
            drawSprite = !drawSprite;
        }
        if (keycode == Input.Keys.F3){
            drawBoxes = !drawBoxes;
        }
        if (keycode == Input.Keys.F4){
            drawText = !drawText;
        }
        if (keycode == Input.Keys.W){
            createScissorsBody("down");
        }
        if (keycode == Input.Keys.S){
            createScissorsBody("up");
        }
        if (keycode == Input.Keys.Q){
            createNeedleBody("up");
        }
        if (keycode == Input.Keys.A){
            createNeedleBody("down");
        }
        if (keycode == Input.Keys.R){
            
            body.setTransform(1.5f,2, 0);
            body.setLinearVelocity(new Vector2(0f,0f));
            body.setAngularVelocity(0);
        }
        if (keycode == Input.Keys.G){
            
            growThread = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer == Buttons.LEFT) {
            posX = screenX - player_sprite.getWidth();
            posY = Gdx.graphics.getHeight() - screenY - player_sprite.getHeight() / 2;

            if (posY > (body.getPosition().y)){
                body.applyForceToCenter(new Vector2(0f,2f), true);                
            } else {
                body.applyForceToCenter(new Vector2(0f,-2f), true);
            }
            body.setTransform(body.getPosition().x, posY/PIXELS_TO_METERS/1.15f, 0);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return true;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean checkPlayerX(int screenX) {
        float mouseX = screenX - player_sprite.getWidth();
        if (mouseX <= Gdx.graphics.getWidth() / 6) {
            return true;
        }
        return false;
    }
    
    public ArrayList<Sprite> createSprites(int length, int startingLength){
        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
        float startingX = player_sprite.getX()-player_sprite.getWidth()/2 - startingLength*player_sprite.getWidth();
        float startingY = player_sprite.getY()-player_sprite.getHeight()/2;
        for (int i = 0; i < length; i++){
            if (backgroundType == 1){
                sprites.add(new Sprite(shortthreadlet));
            } else if (backgroundType == 2){
                Random rand = new Random();
                if (rand.nextBoolean()){
                    sprites.add(new Sprite(shortthreadlet));
                } else {
                    sprites.add(new Sprite(threadlet));
                }
            } else if (backgroundType == 3){
                if (threadSprites.isEmpty()){
                    if (sprites.isEmpty() && length == 1){
                        sprites.add(new Sprite(tail));
                    } else if (length > 1){
                        if (i == length-1){
                            sprites.add(new Sprite(tail));
                        } else {
                            sprites.add(new Sprite(shortthreadlet));
                        }
                    }
                } else {
                    for (int j = 0; j < threadSprites.size(); j++){
                        threadSprites.get(j).setTexture(shortthreadlet);
                    }
                    sprites.add(new Sprite(tail));
                }
            } else if (backgroundType == 4 || backgroundType == 5 || backgroundType == 6){
                sprites.add(new Sprite(shortthreadlet));
            }

            sprites.get(i).setPosition(startingX - sprites.get(i).getWidth(),startingY);
            startingX -= sprites.get(i).getWidth();
        }
        threadSprites.addAll(sprites);
        return sprites;
    }
    
    public void createParticles(int num, float posX, float posY){
        
        for (int i = 0; i < num; i++){
            Sprite particle;
            Random rand = new Random();
            int tex;
            tex = rand.nextInt(5);
            
            if (tex == 0){
                particle = new Sprite(star1);
            } else if (tex == 1){
                particle = new Sprite(star2);                
            }else if (tex == 2){
                particle = new Sprite(star3);                
            }else if (tex == 3){
                particle = new Sprite(star4);                
            }else{
                particle = new Sprite(star5);                
            }
            particle.setPosition(posX + particle.getWidth()/2, posY + particle.getHeight()/2);
            particleSprites.add(particle);
            /*-------------------------------------------------------*/
            BodyDef particleDef = new BodyDef();
            particleDef.type = BodyType.DynamicBody;
            
            particleDef.position.set(particle.getX()/PIXELS_TO_METERS + particle.getWidth()/2 /
                             PIXELS_TO_METERS,
                particle.getY()/PIXELS_TO_METERS + particle.getHeight()/2 / PIXELS_TO_METERS);
        
            /*------------------------------------------------------  */
            
            Body particleBody = world.createBody(particleDef);
            
            CircleShape shape = new CircleShape();
            shape.setRadius(particle.getWidth()/2/PIXELS_TO_METERS);
            shape.setPosition(new Vector2(particle.getX()/PIXELS_TO_METERS + particle.getWidth()/2 / PIXELS_TO_METERS,
                    particle.getY()/PIXELS_TO_METERS + particle.getHeight()/2 / PIXELS_TO_METERS));
            
            FixtureDef threadDef = new FixtureDef();
            threadDef.shape = shape;
            threadDef.density = .1f;
            threadDef.filter.categoryBits = PARTICLE_BIT;
            threadDef.filter.maskBits = PARTICLE_BIT | THREAD_BIT | NEEDLE_BIT;
            
            particleBody.createFixture(threadDef);
            particleBodies.add(particleBody);
            
            switch(tex){
                case 0:
                   // particleBodies.get(particleBodies.size()-1).setTransform(particleBodies.get(particleBodies.size()-1).getPosition().x-.1f, particleBodies.get(particleBodies.size()-1).getPosition().y+.1f,bgx);
                    particleBodies.get(particleBodies.size()-1).applyForceToCenter(new Vector2(.1f, 0f), true);
                    break;
                case 1:
                   // particleBodies.get(particleBodies.size()-1).setTransform(particleBodies.get(particleBodies.size()-1).getPosition().x+.05f, particleBodies.get(particleBodies.size()-1).getPosition().y-.1f,bgx);
                    particleBodies.get(particleBodies.size()-1).applyForceToCenter(new Vector2(.1f, -.1f), true);
                    break;
                case 2:
                   // particleBodies.get(particleBodies.size()-1).setTransform(particleBodies.get(particleBodies.size()-1).getPosition().x+.2f, particleBodies.get(particleBodies.size()-1).getPosition().y-.15f,bgx);
                    particleBodies.get(particleBodies.size()-1).applyForceToCenter(new Vector2(.1f, -.1f), true);
                    break;
                case 3:
                   // particleBodies.get(particleBodies.size()-1).setTransform(particleBodies.get(particleBodies.size()-1).getPosition().x+.15f, particleBodies.get(particleBodies.size()-1).getPosition().y+.15f,bgx);
                    particleBodies.get(particleBodies.size()-1).applyForceToCenter(new Vector2(.1f, .1f), true);
                    break;
                case 4:
                   // particleBodies.get(particleBodies.size()-1).setTransform(particleBodies.get(particleBodies.size()-1).getPosition().x-.2f, particleBodies.get(particleBodies.size()-1).getPosition().y+.2f,bgx);
                    particleBodies.get(particleBodies.size()-1).applyForceToCenter(new Vector2(.1f, .1f), true);
                    break;
            }
            
            shape.dispose();
        }
    }
    
    public Sprite createNeedleSprite(float posX, float posY, String orientation){
       
       Sprite newNeedleSprite;
       Random rand = new Random();
       switch (rand.nextInt(3)){
            case 0:
                newNeedleSprite =  new Sprite(needleGreen);
                break;
            case 1:
                newNeedleSprite =  new Sprite(needleBlue);
                break;
            case 2:
                newNeedleSprite =  new Sprite(needleYellow);
                break;
            default:
                newNeedleSprite =  new Sprite(needleGreen);
                break;
       }
       if ("up".equals(orientation)){
            newNeedleSprite.setPosition(posX - newNeedleSprite.getWidth() / 2, posY - newNeedleSprite.getHeight() / 2);
            newNeedleSprite.setRotation(180);
        } else if ("down".equals(orientation)){
            newNeedleSprite.setPosition(posX - newNeedleSprite.getWidth() / 2, 0 - newNeedleSprite.getHeight() / 2);
            newNeedleSprite.setRotation(0);
        } else {
            newNeedleSprite.setPosition(posX - newNeedleSprite.getWidth() / 2, posY - newNeedleSprite.getHeight() / 2);
            newNeedleSprite.setRotation(90);
        }
       return newNeedleSprite;
    }
    
    public Body createNeedleBody(String orientation){
        float x = width + 200;
        float y = height;
        Sprite needle_sprite;
        
        needle_sprite = createNeedleSprite(x,y, orientation);
        
        needleSprites.add(needle_sprite);
        needleSprites.add(needle_sprite);
        needleSprites.add(needle_sprite);
        
        /*****************************************************************************************************/
        BodyDef needle_bodyDef = new BodyDef();
        needle_bodyDef.type = BodyDef.BodyType.KinematicBody;

        if ("down".equals(orientation)){
            needle_bodyDef.position.set((needle_sprite.getX() + needle_sprite.getWidth()/2) / PIXELS_TO_METERS,
                (needle_sprite.getY() + needle_sprite.getHeight() - 80) / PIXELS_TO_METERS);
        } else if ("up".equals(orientation)){
            needle_bodyDef.position.set((needle_sprite.getX() + needle_sprite.getWidth()/2) / PIXELS_TO_METERS,
                (needle_sprite.getY() + 80) / PIXELS_TO_METERS);
        } else {
            
        }

        Body needle_body = world.createBody(needle_bodyDef);
        PolygonShape needle_shape = new PolygonShape();

        needle_shape.setAsBox(needle_sprite.getWidth()/2 / PIXELS_TO_METERS,
                needle_sprite.getHeight() / 64 / PIXELS_TO_METERS);
        
        FixtureDef needle_fixtureDef = new FixtureDef();
        needle_fixtureDef.shape = needle_shape;
        needle_fixtureDef.density = .1f;
        needle_fixtureDef.filter.categoryBits = NEEDLE_BIT;
        needle_fixtureDef.filter.maskBits = THREAD_BIT;

        if ("up".equals(orientation)){
            needle_body.setTransform(needle_body.getTransform().getPosition(), 110);
        } else if ("down".equals(orientation)){
            needle_body.setTransform(needle_body.getTransform().getPosition(), needle_body.getAngle());
        } else {
            needle_body.setTransform(needle_body.getTransform().getPosition(), 90);
        }
        
        needle_body.createFixture(needle_fixtureDef);
        needleBodies.add(needle_body);
        /*********************************************************************************************************/
        BodyDef needle_hole_bodyDef = new BodyDef();
        needle_hole_bodyDef.type = BodyDef.BodyType.DynamicBody;

        
        if ("down".equals(orientation)){
            needle_hole_bodyDef.position.set((needle_sprite.getX() + needle_sprite.getWidth()/2) / PIXELS_TO_METERS,
                (needle_sprite.getY() + needle_sprite.getHeight()-50) / PIXELS_TO_METERS);
        } else if ("up".equals(orientation)){
            needle_hole_bodyDef.position.set((needle_sprite.getX() + needle_sprite.getWidth()/2) / PIXELS_TO_METERS,
                (needle_sprite.getY() + 50) / PIXELS_TO_METERS);
        } else {
            
        }

        Body needle_hole_body = world.createBody(needle_hole_bodyDef);
        PolygonShape needle_hole_shape = new PolygonShape();

        needle_hole_shape.setAsBox(needle_sprite.getWidth()/2 / PIXELS_TO_METERS,
                needle_sprite.getHeight() / 16 / PIXELS_TO_METERS);
        
        FixtureDef needle_hole_fixtureDef = new FixtureDef();
        needle_hole_fixtureDef.shape = needle_hole_shape;
        needle_hole_fixtureDef.density = .1f;
        needle_hole_fixtureDef.filter.categoryBits = NEEDLE_HOLE_BIT;
        needle_hole_fixtureDef.filter.maskBits = HEAD_BIT;

        if ("up".equals(orientation)){
            needle_hole_body.setTransform(needle_hole_body.getTransform().getPosition(), 110);
        } else if ("down".equals(orientation)){
            needle_hole_body.setTransform(needle_hole_body.getTransform().getPosition(), needle_hole_body.getAngle());
        } else {
            needle_hole_body.setTransform(needle_hole_body.getTransform().getPosition(), 90);
        }
        
        needle_hole_body.createFixture(needle_hole_fixtureDef);
        needleBodies.add(needle_hole_body);
        /*********************************************************************************************************/ 
        BodyDef needle_top_bodyDef = new BodyDef();
        needle_top_bodyDef.type = BodyDef.BodyType.KinematicBody;

        if ("down".equals(orientation)){
            needle_top_bodyDef.position.set((needle_sprite.getX() + needle_sprite.getWidth()/2) / PIXELS_TO_METERS,
                (needle_sprite.getY() + needle_sprite.getHeight() - 20) / PIXELS_TO_METERS);
        } else if ("up".equals(orientation)){
            needle_top_bodyDef.position.set((needle_sprite.getX() + needle_sprite.getWidth()/2) / PIXELS_TO_METERS,
                (needle_sprite.getY() + 20) / PIXELS_TO_METERS);
        } else {
            
        }

        Body needle_top_body = world.createBody(needle_top_bodyDef);
        PolygonShape needle_top_shape = new PolygonShape();

        needle_top_shape.setAsBox(needle_sprite.getWidth()/2 / PIXELS_TO_METERS,
                needle_sprite.getHeight() / 64 / PIXELS_TO_METERS);
        
        FixtureDef needle_top_fixtureDef = new FixtureDef();
        needle_top_fixtureDef.shape = needle_top_shape;
        needle_top_fixtureDef.density = .1f;
        needle_top_fixtureDef.filter.categoryBits = NEEDLE_BIT;
        needle_top_fixtureDef.filter.maskBits = THREAD_BIT;

        if ("up".equals(orientation)){
            needle_top_body.setTransform(needle_top_body.getTransform().getPosition(), 110);
        } else if ("down".equals(orientation)){
            needle_top_body.setTransform(needle_top_body.getTransform().getPosition(), needle_top_body.getAngle());
        } else {
            needle_top_body.setTransform(needle_top_body.getTransform().getPosition(), 90);
        }
        
        needle_top_body.createFixture(needle_top_fixtureDef);
        needleBodies.add(needle_top_body);
        /*********************************************************************************************************/     
        ArrayList<RevoluteJoint> joints = new ArrayList<RevoluteJoint>();
        RevoluteJointDef jointDef = new RevoluteJointDef();

        jointDef.collideConnected = false;
        jointDef.localAnchorA.y = -needle_sprite.getHeight()/8/PIXELS_TO_METERS;
        jointDef.localAnchorB.y = needle_sprite.getHeight()/8/PIXELS_TO_METERS;
        
        jointDef.bodyA = needle_body;
        jointDef.bodyB = needle_hole_body;
        joints.add((RevoluteJoint) world.createJoint(jointDef));
        
        jointDef.bodyA = needle_hole_body;
        jointDef.bodyB = needle_top_body;
        joints.add((RevoluteJoint) world.createJoint(jointDef));
        
        /**********************************************************************************************************/
        needle_shape.dispose();
        needle_top_shape.dispose();
        needle_hole_shape.dispose();
        
        return needle_body;
    }
    
    public Sprite createScissorsSprite(float posX, float posY, String orientation){
       Sprite newScissorSprite =  new Sprite(scissor6);
       if ("down".equals(orientation)){
            newScissorSprite.setPosition(posX - newScissorSprite.getWidth() / 2, posY - newScissorSprite.getHeight() / 2);
            newScissorSprite.setRotation(0);
        } else if ("up".equals(orientation)){
            newScissorSprite.setPosition(posX - newScissorSprite.getWidth() / 2, 0 - newScissorSprite.getHeight() / 2);
            newScissorSprite.setRotation(180);
        } else {
            newScissorSprite.setPosition(posX - newScissorSprite.getWidth() / 2, posY - newScissorSprite.getHeight() / 2);
            newScissorSprite.setRotation(90);
        }
       return newScissorSprite;
    }
    
    public Body createScissorsBody(String orientation){
        float x = width + 200;
        float y = height;
        Sprite scissors_sprite;
        
        scissors_sprite = createScissorsSprite(x,y, orientation);
        
        scissorSprites.add(scissors_sprite);
        
        BodyDef scissors_bodyDef = new BodyDef();
        scissors_bodyDef.type = BodyDef.BodyType.KinematicBody;

        scissors_bodyDef.position.set((scissors_sprite.getX() + scissors_sprite.getWidth()/2) / PIXELS_TO_METERS,
                (scissors_sprite.getY() + scissors_sprite.getHeight()/2) / PIXELS_TO_METERS);

        Body scissors_body = world.createBody(scissors_bodyDef);
        PolygonShape scissors_shape = new PolygonShape();

        scissors_shape.setAsBox(scissors_sprite.getWidth()/32 / PIXELS_TO_METERS,
                scissors_sprite.getHeight() / 4 / PIXELS_TO_METERS);
        
        FixtureDef scissors_fixtureDef = new FixtureDef();
        scissors_fixtureDef.shape = scissors_shape;
        scissors_fixtureDef.density = .1f;
        scissors_fixtureDef.filter.categoryBits = BLADE_BIT;
        scissors_fixtureDef.filter.maskBits = THREAD_BIT;

        if ("down".equals(orientation)){
            scissors_body.setTransform(scissors_body.getTransform().getPosition(), scissors_body.getAngle());
        } else if ("up".equals(orientation)){
            scissors_body.setTransform(scissors_body.getTransform().getPosition(), 110);
        } else {
            scissors_body.setTransform(scissors_body.getTransform().getPosition(), 90);
        }
        
        scissors_body.createFixture(scissors_fixtureDef);
        scissorBodies.add(scissors_body);
        scissors_shape.dispose();
        
        return scissors_body;
    }
    
    public Texture animateScissor(){
        Texture tex;
        if (iterator == 6){
            if (timer >= timerpaceOpen){
                lastiterator = 6;
                iterator = 5;
                timer = 0;
            } else {
                timer++;
            }
        } else if (iterator == 1){
            if (timer >= timerpaceClosed){
                lastiterator = 1;
                iterator = 2;
                timer = 0;
            } else {
                timer++;
            }
        } else if (iterator > lastiterator){
            iterator++;
            lastiterator++;
        } else {
            iterator--;
            lastiterator--;
        }
        
        switch (iterator){
                case 1:
                    tex = scissor1;
                    break;
                case 2:
                    tex = scissor2;
                    break;
                case 3:
                    tex = scissor3;
                    break;
                case 4:
                    tex = scissor4;
                    break;
                case 5:
                    tex = scissor5;
                    break;
                default:
                    tex = scissor6;
                    break;
        }
        for (Sprite scissor_sprite : scissorSprites){
            scissor_sprite.setTexture(tex);
        }
        return tex;
    }
    
    public ArrayList<Body> createRope(ArrayList<Sprite> sprites, int startingLength){
        ArrayList<Body> segments = new ArrayList<Body>();
        
        ArrayList<RopeJoint> ropeJoints = new ArrayList<RopeJoint>();
        //ArrayList<RopeJoint> ropeJoints = new ArrayList<RopeJoint>();
        
        BodyDef segmentDef = new BodyDef();
        segmentDef.type = BodyType.DynamicBody;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprites.get(0).getWidth()/2 / PIXELS_TO_METERS, sprites.get(0).getHeight()
                        /2 / PIXELS_TO_METERS);
        
        boolean guided = prefs.getBoolean("guide", false);
        
        for (int i = 0; i < sprites.size(); i++){
            segments.add(world.createBody(segmentDef));
            segmentDef.position.set((sprites.get(i).getX() + sprites.get(i).getWidth()/2) /
                             PIXELS_TO_METERS,
                (sprites.get(i).getY() + sprites.get(i).getHeight()/2) / PIXELS_TO_METERS);
            FixtureDef threadDef = new FixtureDef();
            threadDef.shape = shape;
            threadDef.density = .1f;
            threadDef.filter.categoryBits = THREAD_BIT;
            threadDef.filter.maskBits = SCISSOR_BIT | BLADE_BIT | NEEDLE_BIT;
            
            segments.get(i).createFixture(threadDef);
            if(guided){
                segments.get(i).setLinearDamping(.5f);
                segments.get(i).setAngularDamping(.5f);
            }
            
        }
        
        if(guided){
            segments.get(segments.size()-1).setAngularDamping(2.5f);
            segments.get(segments.size()-1).setLinearDamping(2.5f);
        }
        
        
        shape.dispose();
        //DistanceJointDef the_joint = new DistanceJointDef();
        
        RevoluteJointDef jointDef = new RevoluteJointDef();
        RopeJointDef ropeJointDef = new RopeJointDef();
        //RopeJointDef ropeJointDef = new RopeJointDef();
        jointDef.collideConnected = false;
        jointDef.localAnchorA.x = -sprites.get(0).getWidth()/2/PIXELS_TO_METERS;
        jointDef.localAnchorB.x = sprites.get(0).getWidth()/2/PIXELS_TO_METERS;
        
        
        ropeJointDef.collideConnected = false;
        ropeJointDef.localAnchorA.x = -sprites.get(0).getWidth()/2/PIXELS_TO_METERS;
        ropeJointDef.localAnchorB.x = sprites.get(0).getWidth()/2/PIXELS_TO_METERS;
        ropeJointDef.maxLength = sprites.get(0).getWidth()/PIXELS_TO_METERS;
        //ropeJointDef.localAnchorA.x = -sprites.get(0).getWidth()/2/PIXELS_TO_METERS;
        //ropeJointDef.localAnchorB.x = sprites.get(0).getWidth()/2/PIXELS_TO_METERS;
        
        
        for (int i = 0; i < sprites.size()-1; i++){
            jointDef.bodyA = segments.get(i);
            jointDef.bodyB = segments.get(i + 1);
            //jointDef.initialize(segments.get(i), segments.get(i+1), body.getPosition());
            joints.add((RevoluteJoint) world.createJoint(jointDef));
            
            ropeJointDef.bodyA = segments.get(i);
            ropeJointDef.bodyB = segments.get(i + 1);
            ropeJoints.add((RopeJoint) world.createJoint(ropeJointDef));
            
            //the_joint.localAnchorA.x = -sprites.get(0).getWidth()/2/PIXELS_TO_METERS;
            //the_joint.localAnchorB.x = sprites.get(0).getWidth()/2/PIXELS_TO_METERS;
            //the_joint.initialize(segments.get(i), segments.get(i +1), new Vector2(2,2), new Vector2(2,2));
            //the_joint.collideConnected = false;
            //world.createJoint(the_joint);
            //ropeJointDef.bodyA = segments.get(i);
            //ropeJointDef.bodyB = segments.get(i + 1);
            //ropeJoints.add((RopeJoint) world.createJoint(ropeJointDef));
        }
        if (startingLength == 0){
            jointDef.bodyA = body;        
            jointDef.bodyB = segments.get(0);
            joints.add((RevoluteJoint) world.createJoint(jointDef));
            
            ropeJointDef.bodyA = body;
            ropeJointDef.bodyB = segments.get(0);
            ropeJoints.add((RopeJoint) world.createJoint(ropeJointDef));
        } else {
            jointDef.bodyA = threadBodies.get(threadBodies.size()-1);        
            jointDef.bodyB = segments.get(0);
            joints.add((RevoluteJoint) world.createJoint(jointDef));
            
            ropeJointDef.bodyA = threadBodies.get(threadBodies.size() -1);
            ropeJointDef.bodyB = segments.get(0);
            ropeJoints.add((RopeJoint) world.createJoint(ropeJointDef));
        }
        return segments;
    }
    
    public void spawn(){
        this.beats++;
        Random rand = new Random();
        int randNum = rand.nextInt(SPAWN_RATE);
        //System.out.println(randNum);
        switch (randNum){
            case 0:
                if (bottomSpacer <= 0){
                    createNeedleBody("down");
                    this.needles++;
                    lastRand = 0;
                    bottomSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 1:
                if (topSpacer <= 0){
                    createNeedleBody("up");
                    this.needles++;
                    lastRand = 1;
                    topSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 2:
                if (topSpacer <= 0){
                    createScissorsBody("down");
                    this.scissors++;
                    lastRand = 2;
                    topSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 3:
                if (bottomSpacer <= 0){
                    createScissorsBody("up");
                    this.scissors++;
                    lastRand = 3;
                    bottomSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 4:
                if (bottomSpacer <= 0){
                    createScissorsBody("up");
                    this.scissors++;
                    lastRand = 4;
                    bottomSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 5:
                if (topSpacer <= 0){
                    createScissorsBody("down");
                    this.scissors++;
                    lastRand = 5;
                    topSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 6:
                if (topSpacer <= 0){
                    createNeedleBody("up");
                    this.needles++;
                    lastRand = 1;
                    topSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 7:
                if (bottomSpacer <= 0){
                    createNeedleBody("down");
                    this.needles++;
                    lastRand = 0;
                    bottomSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            default:
                break;
        }
        
    }
    
    public void spawn(int num){
        this.beats++;
        //Random rand = new Random();
        //int randNum = rand.nextInt(SPAWN_RATE);
        //System.out.println(randNum);
        switch (num){
            case 0:
                if (bottomSpacer <= 0){
                    createNeedleBody("down");
                    this.needles++;
                    lastRand = 0;
                    bottomSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 1:
                if (topSpacer <= 0){
                    createNeedleBody("up");
                    this.needles++;
                    lastRand = 1;
                    topSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 2:
                if (topSpacer <= 0){
                    createScissorsBody("down");
                    this.scissors++;
                    lastRand = 2;
                    topSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 3:
                if (bottomSpacer <= 0){
                    createScissorsBody("up");
                    this.scissors++;
                    lastRand = 3;
                    bottomSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            case 4:
                if (bottomSpacer <= 0){
                    createScissorsBody("up");
                    this.scissors++;
                    lastRand = 3;
                    bottomSpacer = SPACER_AMOUNT;
                }
                topSpacer--;
                bottomSpacer--;
                break;
            default:
                break;
        }
        
    }

    @Override
    public void show() {
        //Gets track from Spotify
        //gets height and width
        width = 1280;
        height = 720;
        //System.out.println("Width: " + width + ", Height: " + height);
        
        camera = new OrthographicCamera(width,height);

        //camera.viewportWidth = width/PIXELS_TO_METERS;
        //camera.viewportHeight = height/PIXELS_TO_METERS;
        camera.position.set(width/2f, height/2f, 0);
        viewport = new StretchViewport(width, height, camera);

        if (backgroundType == 1){
        bgx = 144;
        bgcolorx = 2016;
        } else if (backgroundType == 2 || backgroundType == 3){
            bgx = 0;
            bgcolorx = 0;
            bgcloudx = 0;
        }
        
        //BACKGROUND_SPEED = tempo/60f*3f;
        SCROLLING_FOREGROUND_SPEED = tempo/60f*-3f;
        
        batch = new SpriteBatch();
        
        Random rand = new Random();
        int texType = rand.nextInt(3);
        if (backgroundType == 2){
            texType = 3;
        } else if (backgroundType == 3){
            texType = 4;
        } else if (backgroundType == 4 || backgroundType == 5){
            texType = 5;
        }else if (backgroundType == 6){
            texType = 6;
        }
        switch (texType){
            case 0:
                shortthreadlet = new Texture("shortthreadhighcontrast_alt.png");
                threadlet = new Texture("shortthreadhighcontrast2_alt.png");
                break;
            case 1:
                shortthreadlet = new Texture("shortthreadhighcontrast_purp.png");
                threadlet = new Texture("shortthreadhighcontrast2_purp.png");
                break;
            case 2:
                shortthreadlet = new Texture("shortthreadhighcontrast.png");
                threadlet = new Texture("shortthreadhighcontrast2.png");
                break;
            case 3:
                shortthreadlet = new Texture("shortthreadhighcontrast_wood.png");
                threadlet = new Texture("shortthreadhighcontrast2_wood.png");
                break;
            case 4:
                shortthreadlet = new Texture("shortthreadhighcontrast_snake.png");
                threadlet = new Texture("shortthreadhighcontrast2_snake2.png");
                tail = new Texture("shortthreadhighcontrast_snaketail.png");
                break;
            case 5:
                shortthreadlet = new Texture("shortthreadhighcontrast_staff2.png");
                threadlet = new Texture("shortthreadhighcontrast2_staff2.png");
                break;
            case 6:
                shortthreadlet = new Texture("shortthreadhighcontrast_fish.png");
                threadlet = new Texture("shortthreadhighcontrast_fish.png");
                break;
            default:
                break;
        }
        
        
        
        redlet = new Texture("redthread.png");
        
        background = new Texture("tallback.png");
        threadedBackground = new Texture("threadstall.png");
        colorBackground = new Texture("rainbow.png");
        shadowBackground = new Texture("shadowmap3.png");
        
        woodsBackground = new Texture("woodssmallbackground.png");
        woodsBack = new Texture("woodsback.png");
        woodsFront = new Texture("woodsfront.png");
        woodsClouds = new Texture("woodsclouds.png");
        
        if (backgroundType == 1){
            needleGreen = new Texture("needlegreenoutline.png");
            needleBlue = new Texture("needleblueoutline.png");
            needleYellow = new Texture("needleyellowoutline.png");
            
            scissor1 = new Texture("scissor1.png");
            scissor2 = new Texture("scissor2.png");
            scissor3 = new Texture("scissor3.png");
            scissor4 = new Texture("scissor4.png");
            scissor5 = new Texture("scissor5.png");
            scissor6 = new Texture("scissor6.png");
            
            star1 = new Texture("star1.png");
            star2 = new Texture("star2.png");
            star3 = new Texture("star3.png");
            star4 = new Texture("star4.png");
            star5 = new Texture("star5.png");
        } else if (backgroundType == 2){
            woodsBackground = new Texture("woodssmallbackground.png");
            woodsBack = new Texture("woodsback.png");
            woodsFront = new Texture("woodsfront.png");
            woodsClouds = new Texture("woodsclouds.png");

            needleGreen = new Texture("woodspinetreebird.png");
            needleBlue = new Texture("woodspinetreebird.png");
            needleYellow = new Texture("woodspinetreebird.png");
            emptyTree = new Texture("woodspinetree.png");
            
            scissor1 = new Texture("saw1.png");
            scissor2 = new Texture("saw2.png");
            scissor3 = new Texture("saw3.png");
            scissor4 = new Texture("saw4.png");
            scissor5 = new Texture("saw5.png");
            scissor6 = new Texture("saw6.png");
            
            star1 = new Texture("wood1.png");
            star2 = new Texture("wood2alt.png");
            star3 = new Texture("wood3alt.png");
            star4 = new Texture("wood4.png");
            star5 = new Texture("wood5.png");
        } else if (backgroundType == 3){
            woodsBackground = new Texture("desertsmallbackground.png");
            woodsBack = new Texture("desertback.png");
            woodsFront = new Texture("desertfront.png");
            woodsClouds = new Texture("desertsky.png");
        
            needleGreen = new Texture("skeletonhand.png");
            needleBlue = new Texture("skeletonhand2.png");
            needleYellow = new Texture("skeletonhand3.png");
            
            scissor1 = new Texture("sword1.png");
            scissor2 = new Texture("sword2.png");
            scissor3 = new Texture("sword3.png");
            scissor4 = new Texture("sword4.png");
            scissor5 = new Texture("sword5.png");
            scissor6 = new Texture("sword6.png");
            
            star1 = new Texture("wood1.png");
            star2 = new Texture("wood2alt.png");
            star3 = new Texture("wood3alt.png");
            star4 = new Texture("wood4.png");
            star5 = new Texture("wood4.png");
        } else if (backgroundType == 4){
            woodsBackground = new Texture("clubbackgroundsmall.png");
            woodsBack = new Texture("clubback.png");
            woodsFront = new Texture("clubback.png");
            woodsClouds = new Texture("desertsky.png");
        
            needleGreen = new Texture("microphone.png");
            needleBlue = new Texture("microphone.png");
            needleYellow = new Texture("microphone.png");
            
            scissor1 = new Texture("trombone1.png");
            scissor2 = new Texture("trombone2.png");
            scissor3 = new Texture("trombone3.png");
            scissor4 = new Texture("trombone4.png");
            scissor5 = new Texture("trombone5.png");
            scissor6 = new Texture("trombone6.png");
            
            star1 = new Texture("wood1.png");
            star2 = new Texture("wood2alt.png");
            star3 = new Texture("wood3alt.png");
            star4 = new Texture("wood4.png");
            star5 = new Texture("wood4.png");
        }else if (backgroundType == 5){
            woodsBackground = new Texture("clubbackgroundsmall2.png");
            woodsBack = new Texture("clubback.png");
            woodsFront = new Texture("clubback.png");
            woodsClouds = new Texture("desertsky.png");
        
            needleGreen = new Texture("microphone.png");
            needleBlue = new Texture("microphone.png");
            needleYellow = new Texture("microphone.png");
            
            scissor1 = new Texture("trombone1.png");
            scissor2 = new Texture("trombone2.png");
            scissor3 = new Texture("trombone3.png");
            scissor4 = new Texture("trombone4.png");
            scissor5 = new Texture("trombone5.png");
            scissor6 = new Texture("trombone6.png");
            
            star1 = new Texture("wood1.png");
            star2 = new Texture("wood2alt.png");
            star3 = new Texture("wood3alt.png");
            star4 = new Texture("wood4.png");
            star5 = new Texture("wood4.png");
        } else if (backgroundType == 6){
            woodsBackground = new Texture("oceanbackground.png");
            woodsBack = new Texture("oceanmiddleground.png");
            woodsFront = new Texture("oceanforeground.png");
            
            needleGreen = new Texture("bubblestickpopped.png");
            needleBlue = new Texture("bubblestickpopped.png");
            needleYellow = new Texture("bubblestickpopped.png");
            emptyTree = new Texture("bubblestick.png");
            
            scissor1 = new Texture("spear1.png");
            scissor2 = new Texture("spear2.png");
            scissor3 = new Texture("spear3.png");
            scissor4 = new Texture("spear4.png");
            scissor5 = new Texture("spear5.png");
            scissor6 = new Texture("spear6.png");
            
            star1 = new Texture("star1.png");
            star2 = new Texture("star2.png");
            star3 = new Texture("star3.png");
            star4 = new Texture("star4.png");
            star5 = new Texture("star5.png");
        }
        font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),Gdx.files.internal("fonts/font.png"),false);
        blackfont = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),Gdx.files.internal("fonts/blackfont.png"),false);
        
        timerpaceClosed = (300 - tempo)/60 * 1;
        timerpaceOpen = (300 - tempo)/60 * 12;
        
        player_sprite = new Sprite(threadlet);

        player_sprite.setPosition(width/3-player_sprite.getWidth()/2,height/2-player_sprite.getHeight()/2);
   
        world = new World(new Vector2(-10f, 0), true);
        /*----------------------------------------------------------------*/
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;

        bodyDef.position.set((player_sprite.getX() + player_sprite.getWidth()/2) /
                             PIXELS_TO_METERS,
                (player_sprite.getY() + player_sprite.getHeight()/2) / PIXELS_TO_METERS);

        body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(player_sprite.getWidth()/2 / PIXELS_TO_METERS, player_sprite.getHeight()
                       /2 / PIXELS_TO_METERS);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = .1f;
        fixtureDef.filter.categoryBits = HEAD_BIT;
        fixtureDef.filter.maskBits = NEEDLE_HOLE_BIT;
        
        body.createFixture(fixtureDef);
        body.setLinearDamping(.5f);
        body.setAngularDamping(.5f);

        shape.dispose();
        /*----------------------------------------------------------------*/
        debugRenderer = new Box2DDebugRenderer();
        
        lastTimeBg = TimeUtils.nanoTime();
        lastTimeTempo = TimeUtils.nanoTime();

        Gdx.input.setInputProcessor(this);
        
        
        threadSprites = createSprites(STARTING_LENGTH, 0);
        threadBodies = createRope(threadSprites, 0);
        
        world.setContactListener(new ContactListener() {

            //Called when two box2d objects come into contact with one another
            //Each object is returned as a fixture
            @Override
            public void beginContact(Contact contact) {
                
                if (contact.getFixtureB().getFilterData().categoryBits == THREAD_BIT 
                        && contact.getFixtureA().getFilterData().categoryBits == BLADE_BIT && jointDestroyable){
                    for (int i = 0; i < contact.getFixtureB().getBody().getJointList().size; i++){
                        if (!jointDeletionList.contains(contact.getFixtureB().getBody().getJointList().get(i))){
                            jointDeletionList.add(contact.getFixtureB().getBody().getJointList().get(i));
                            needle_combo = 0;
                            jointDestroyable = false;
                        }
                    }
                }
                if (contact.getFixtureA().getFilterData().categoryBits == THREAD_BIT 
                        && contact.getFixtureB().getFilterData().categoryBits == BLADE_BIT&& jointDestroyable){
                    for (int i = 0; i < contact.getFixtureA().getBody().getJointList().size; i++){
                        if (!jointDeletionList.contains(contact.getFixtureA().getBody().getJointList().get(i))){
                            jointDeletionList.add(contact.getFixtureA().getBody().getJointList().get(i));
                            needle_combo = 0;
                            jointDestroyable = false;
                        }
                    }
                }
                
                if ((contact.getFixtureA().getFilterData().categoryBits == HEAD_BIT 
                        && contact.getFixtureB().getFilterData().categoryBits == NEEDLE_HOLE_BIT)){
                    contact.getFixtureB().getFilterData().categoryBits = NO_COLLIDE_BIT;
                    contact.getFixtureB().getFilterData().maskBits = NO_COLLIDE_BIT;
                    if (growableAllowed && needle_hit >= GROWTH_SUPRESSOR && growthTimer <= 0){
                        if (backgroundType == 2 || backgroundType == 6){
                            needleSprites.get(needleBodies.indexOf(contact.getFixtureB().getBody())).setTexture(emptyTree);
                        }
                        growThread = true;
                        needle_combo++;
                        needle_hit = 0;
                        growableAllowed = false;
                        growthTimer = GROWTH_TIMER_OFFSET;
                        particlesAllowed = true;
                    } else if (growableAllowed && needle_hit < GROWTH_SUPRESSOR) {
                        needle_hit++;
                    }
                } else if ((contact.getFixtureB().getFilterData().categoryBits == HEAD_BIT 
                        && contact.getFixtureA().getFilterData().categoryBits == NEEDLE_HOLE_BIT)){
                    contact.getFixtureA().getFilterData().categoryBits = NO_COLLIDE_BIT;
                    contact.getFixtureA().getFilterData().maskBits = NO_COLLIDE_BIT;
                    if (growableAllowed && needle_hit >= GROWTH_SUPRESSOR && growthTimer <= 0){
                        if (backgroundType == 2 || backgroundType == 6){
                            needleSprites.get(needleBodies.indexOf(contact.getFixtureA().getBody())).setTexture(emptyTree);
                        }
                        growThread = true;
                        needle_combo++;
                        needle_hit = 0;
                        growableAllowed = false;
                        growthTimer = GROWTH_TIMER_OFFSET;
                        particlesAllowed = true;
                    } else if (growableAllowed && needle_hit < GROWTH_SUPRESSOR) {
                        needle_hit++;
                    }
                }
                
            }

            //Called when two box2d objects move away from each other after contact
            //Each object is returned as a fixture
            @Override
            public void endContact(Contact contact) {
                
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                //System.out.println("Presolve: #" + counter++);
                if (contact.getFixtureB().getFilterData().categoryBits == THREAD_BIT && contact.getFixtureA().getFilterData().categoryBits == BLADE_BIT && jointDestroyable){
                    for (int i = 0; i < contact.getFixtureB().getBody().getJointList().size; i++){
                        if (!jointDeletionList.contains(contact.getFixtureB().getBody().getJointList().get(i))){
                            jointDeletionList.add(contact.getFixtureB().getBody().getJointList().get(i));
                            needle_combo = 0;
                            jointDestroyable = false;
                        }
                    }
                    
                }
                if (contact.getFixtureA().getFilterData().categoryBits == THREAD_BIT && contact.getFixtureB().getFilterData().categoryBits == BLADE_BIT&& jointDestroyable){
                    for (int i = 0; i < contact.getFixtureA().getBody().getJointList().size; i++){
                        if (!jointDeletionList.contains(contact.getFixtureA().getBody().getJointList().get(i))){
                            jointDeletionList.add(contact.getFixtureA().getBody().getJointList().get(i));
                            needle_combo = 0;
                            jointDestroyable = false;
                        }
                    }
                }
                
                
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                //System.out.println("postsolve");
            }
    });
        m.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                endLevel();
                songTitle="ONCOMPLETE TRIGGERED";
            }
        });
        
        if(m != null){
            float volume = prefs.getFloat("musicvol", 1);
            m.setVolume(volume/20);
            m.play();
        }
    }

    @Override
    public void render(float f) {
        camera.update();
        world.step(1f/60f, 6, 2);
        
        body.applyTorque(torque,true);
        
        float ydir = posY - (body.getPosition().y* PIXELS_TO_METERS);
        
        if (ydir <= 5 && ydir >= -5){
            body.setLinearVelocity(0f, 0f);
        }
        
        player_sprite.setPosition((body.getPosition().x * PIXELS_TO_METERS) - player_sprite.getWidth()/2,
                (body.getPosition().y * PIXELS_TO_METERS) -player_sprite.getHeight()/2);
        
        player_sprite.setRotation((float)Math.toDegrees(body.getAngle()));
        for (int i = 0; i < scissorSprites.size(); i++){
            scissorSprites.get(i).setPosition((scissorBodies.get(i).getPosition().x * PIXELS_TO_METERS) - scissorSprites.get(i).getWidth()/2 ,
                (scissorSprites.get(i).getY()));
        
            scissorSprites.get(i).setRotation((float)Math.toDegrees(scissorBodies.get(i).getAngle()));
        }
        
        for (int i = 0; i < needleSprites.size(); i++){
            needleSprites.get(i).setPosition((needleBodies.get(i).getPosition().x * PIXELS_TO_METERS) - needleSprites.get(i).getWidth()/2 ,
                (needleSprites.get(i).getY()));
        
            needleSprites.get(i).setRotation((float)Math.toDegrees(needleBodies.get(i).getAngle()));
            
            if ( needleSprites.get(i).getX() <= 0){
                //queueToRemove.add(new Point(i,2));
            }
        }
        for (int i = 0; i < threadSprites.size(); i++) {
            if (i == 0) {
                threadSprites.get(i).setPosition((threadBodies.get(i).getPosition().x * PIXELS_TO_METERS) - threadSprites.get(i).getWidth() / 2,
                        (threadBodies.get(i).getPosition().y * PIXELS_TO_METERS) - threadSprites.get(i).getHeight() / 2);
            } else {
                float diff = threadSprites.get(i).getY() - threadSprites.get(i - 1).getY();
                if (diff > 10 || diff < -10) {
                    threadSprites.get(i).setPosition((threadBodies.get(i).getPosition().x * PIXELS_TO_METERS) - threadSprites.get(i).getWidth() / 2,
                            (threadBodies.get(i).getPosition().y * PIXELS_TO_METERS) + diff - threadSprites.get(i).getHeight() / 2);
                }
                threadSprites.get(i).setPosition((threadBodies.get(i).getPosition().x * PIXELS_TO_METERS) - threadSprites.get(i).getWidth() / 2,
                        (threadBodies.get(i).getPosition().y * PIXELS_TO_METERS) - threadSprites.get(i).getHeight() / 2);
            }
            threadSprites.get(i).setRotation((float) Math.toDegrees(threadBodies.get(i).getAngle()));

            if (threadSprites.get(i).getX() <= -25 || threadBodies.get(i).getPosition().x <= -25) {
                queueToRemove.add(new Vector2(i, 1));
            }
        }
        
        for (int i = 0; i < particleSprites.size(); i++){
            particleSprites.get(i).setPosition((particleBodies.get(i).getPosition().x * PIXELS_TO_METERS) - particleSprites.get(i).getWidth()/2 ,
                    (particleBodies.get(i).getPosition().y * PIXELS_TO_METERS) - particleSprites.get(i).getHeight()/2);
            
            particleSprites.get(i).setRotation((float)Math.toDegrees(particleBodies.get(i).getAngle()));
            
            if (particleSprites.get(i).getX() <= -50){
                queueToRemove.add(new Vector2(i,3));
            }
        }
        
        
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (TimeUtils.nanoTime() - lastTimeBg > 50000000) {
            
        }
        
        int currentBeat = (int) (m.getPosition() / (512.0/44100.0));
            
            if(currentBeat < peaks.get(0).size() && currentBeat > beatIndex){
                for(int i = beatIndex+1;i<=currentBeat;i++){
                    
                    if(peaks.get(0).get(i) > 0 && i - lastDisplayed > displayInterval){
                        //System.out.println(peaks.get(0).get(i));
                        Random rand = new Random();
                        int randNum = rand.nextInt(2)+2;
                        spawn(randNum);
                        lastDisplayed = i;
                    }
                    if(peaks.get(1).get(i) > 0 && i - lastDisplayed > displayInterval){
                        //System.out.println(peaks.get(0).get(i));
                        Random rand = new Random();
                        int randNum = rand.nextInt(2);
                        spawn(randNum);
                        lastDisplayed = i;
                    }
                    if(peaks.get(2).get(i) > 0 && i - lastDisplayed > displayInterval){
                        //System.out.println(peaks.get(0).get(i));
                        Random rand = new Random();
                        int randNum = rand.nextInt(2);
                        spawn(randNum);
                        lastDisplayed = i;
                    }
                }
                
                beatIndex = currentBeat;
            }
            
        if (TimeUtils.nanoTime() - lastTimeTempo > (100000000 * 60)/tempo) {
            //System.out.println("Spawn");
            //spawn();
            lastTimeTempo = TimeUtils.nanoTime();
            animateScissor();
        }
        
        if (TimeUtils.nanoTime() - lastTimeBg > 100000000) {
            runtimeCounter++;
            growthTimer--;
            if (backgroundType == 1){
                bgx -= BACKGROUND_SPEED;
                if (HYPERTHREADING_MODE){
                    bgcolorx -= BACKGROUND_SPEED*2;
                } else {
                    bgcolorx -= BACKGROUND_SPEED;
                }
            } else if (backgroundType == 2 || backgroundType == 3 || backgroundType == 4 || backgroundType == 5){
                if (HYPERTHREADING_MODE){
                    bgcolorx -= BACKGROUND_SPEED*2f;
                    bgcloudx -= BACKGROUND_SPEED*2f;
                    bgx -= BACKGROUND_SPEED;
                } else {
                    bgcolorx -= BACKGROUND_SPEED;
                    bgcloudx -= BACKGROUND_SPEED;
                    bgx -= BACKGROUND_SPEED * .5f;
                }
            } else if (backgroundType == 6){
                if (HYPERTHREADING_MODE){
                    bgcolorx -= BACKGROUND_SPEED*2f;
                    //bgcloudx -= BACKGROUND_SPEED*2f;
                    bgx -= BACKGROUND_SPEED;
                } else {
                    bgcolorx -= BACKGROUND_SPEED;
                    //bgcloudx -= BACKGROUND_SPEED;
                    bgx -= BACKGROUND_SPEED * .5f;
                }
            }
            for (int i = 0; i < scissorBodies.size(); i++){
                scissorBodies.get(i).setLinearVelocity(new Vector2(SCROLLING_FOREGROUND_SPEED,0f));
                if (scissorSprites.get(i).getY() >= 0){
                    if (scissorSprites.get(i).getTexture() == scissor1 || scissorSprites.get(i).getTexture() == scissor2){
                        scissorBodies.get(i).setTransform(scissorBodies.get(i).getPosition().x, (scissorSprites.get(i).getY() + scissorSprites.get(i).getHeight()*.25f)/PIXELS_TO_METERS, scissorBodies.get(i).getAngle());
                    } else {
                        scissorBodies.get(i).setTransform(scissorBodies.get(i).getPosition().x, 15, scissorBodies.get(i).getAngle());
                    }
                } else {
                    if (scissorSprites.get(i).getTexture() == scissor1 || scissorSprites.get(i).getTexture() == scissor2){
                        //System.out.println((scissorSprites.get(i).getY() + scissorSprites.get(i).getHeight()/2)/PIXELS_TO_METERS);
                        scissorBodies.get(i).setTransform(scissorBodies.get(i).getPosition().x, (scissorSprites.get(i).getY() + scissorSprites.get(i).getHeight()*.75f)/PIXELS_TO_METERS, scissorBodies.get(i).getAngle());
                    } else {
                        scissorBodies.get(i).setTransform(scissorBodies.get(i).getPosition().x, 15, scissorBodies.get(i).getAngle());
                    }
                }

                if (scissorSprites.get(i).getX() <= -220) {
                   queueToRemove.add(new Vector2(i,0));
                }
            }
            
            for (Body needleBody : needleBodies) {
                needleBody.setLinearVelocity(new Vector2(SCROLLING_FOREGROUND_SPEED,0f));
                //System.out.println("needlex = " + needleBody.getPosition().x + " and " + width/5/PIXELS_TO_METERS);
                if (needleBody.getPosition().x <= width/2.9f/PIXELS_TO_METERS) {
                    for (int i = 0; i < needleBody.getFixtureList().size; i++){
                        Filter filt = needleBody.getFixtureList().get(i).getFilterData();
                        filt.maskBits = NO_COLLIDE_BIT;
                        filt.categoryBits = NO_COLLIDE_BIT;
                        needleBody.getFixtureList().get(i).setFilterData(filt);
                    }
                }
            }
            
            if (particlesAllowed){
                createParticles(15,body.getPosition().x*PIXELS_TO_METERS,body.getPosition().y*PIXELS_TO_METERS);
                particlesAllowed = false;
            }
            
            if (growThread){
                //System.out.println("Trying to grow Thread");
                if (threadBodies.size() < MAX_THREAD_LENGTH && threadSprites.size() < MAX_THREAD_LENGTH){
                    //System.out.println("Body Size before: " + threadBodies.size());
                    //System.out.println("Sprite Size before: " + threadSprites.size());
                    threadBodies.addAll(createRope(createSprites(1, threadBodies.size()), threadBodies.size()));
                    //System.out.println("Body Size after: " + threadBodies.size());
                    //System.out.println("Sprite Size after: " + threadSprites.size());
                }
                needles_thread++;
                growThread = false;
                growableAllowed = true;
            }
            if (queueToRemove.size() > 0){
                //System.out.println("Queue Size: " + queueToRemove.size());
                for (int i = queueToRemove.size()-1; i >= 0; i--){
                    //System.out.println("Item in Queue: " + queueToRemove.get(i).x + "," + queueToRemove.get(i).y);
                    int ref = (int) queueToRemove.get(i).x;
                    if (queueToRemove.get(i).y == 0){
                        scissorBodies.remove(scissorBodies.get(ref));
                        scissorSprites.remove(scissorSprites.get(ref));
                    } else if (queueToRemove.get(i).y == 1){
                        if (ref < threadBodies.size()){
                            threadBodies.get(ref).getJointList().clear();
                            threadBodies.remove(threadBodies.get(ref));
                        }
                        if (ref < threadSprites.size()){
                            threadSprites.remove(threadSprites.get(ref));
                            this.thread_cut++;
                        }
                    } else if (queueToRemove.get(i).y == 2){
                        if (ref < needleBodies.size()){
                            needleBodies.remove(needleBodies.get(ref));
                        }
                        if (ref < needleSprites.size()){
                            needleSprites.remove(needleSprites.get(ref));
                        }
                    } else if (queueToRemove.get(i).y == 3){
                        if (ref < particleSprites.size()){
                            particleSprites.remove(particleSprites.get(ref));
                        }
                        if (ref < particleBodies.size()){
                            particleBodies.remove(particleBodies.get(ref));
                        }
                    }
                }
                queueToRemove.clear();
            }
            lastTimeBg = TimeUtils.nanoTime();
        }

        if (bgx <= 0) {
            if (backgroundType == 1){
                bgx = 144;
            } else if (backgroundType == 2 || backgroundType == 3 || backgroundType == 4 || backgroundType == 5 || backgroundType == 6){
                bgx = 1035;
            }
        }
        if (bgcolorx <= 0){
            if (backgroundType == 1){
                bgcolorx = 2016;
            } else if (backgroundType == 2 || backgroundType == 3 || backgroundType == 4 || backgroundType == 5 || backgroundType == 6){
                bgcolorx = 1035;
            }
        }
        if (backgroundType == 2 || backgroundType == 3 || backgroundType == 4 || backgroundType == 5){
            if (bgcloudx <= 0){
                bgcloudx = 3000;
            }
        }
        
        batch.setProjectionMatrix(camera.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
                      PIXELS_TO_METERS, 0);
        
        if (jointDeletionList.size() > 0){
            for (JointEdge jointEdge : jointDeletionList) {
                Joint joint = jointEdge.joint;
                world.destroyJoint(joint);
                joint.setUserData(null);
                joint = null;    
            }
            jointDeletionList.clear();
            jointDestroyable = true;
        }
        if (threadBodies.isEmpty() && body.getJointList().size > 0){
            world.destroyJoint(body.getJointList().peek().joint);
        }
        
        //System.out.println(threadBodies.size() + " + " + MAX_THREAD_LENGTH);
        if ((int)threadBodies.size() == MAX_THREAD_LENGTH){
            HYPERTHREADING_MODE = true;
            //BACKGROUND_SPEED = tempo/60f*3f * 2f;
            BACKGROUND_SPEED = 28.8f;
            SCROLLING_FOREGROUND_SPEED = tempo/60f*-2f * 2f;
            if (needle_combo < 5){
                needle_combo = 5;
            }
        } else {
            HYPERTHREADING_MODE = false;
            //BACKGROUND_SPEED = tempo/60f*3f;
            BACKGROUND_SPEED = 14.4f;
            SCROLLING_FOREGROUND_SPEED = tempo/60f*-2f;
        }
        if (!m.isPlaying()){
            hyperthreading = "FINISH";
            endLevel();
        } else {
            if (runtimeCounter >= 30){
                songTitle = songArtist;
            }
            if (runtimeCounter >= 60){
                songTitle = "";
            }
            if (HYPERTHREADING_MODE){
                score += SCORE_CONSTANT * threadBodies.size() * needle_combo;
                score_amount = lbl_score.toString() + score;
                multiplier = "";
                bonus = "BONUS: x" + needle_combo;
                //hyperthreading = "HYPERTHREADING MODE";
            } else {
                score += SCORE_CONSTANT * threadBodies.size();
                score_amount = lbl_score.toString() + score;
                multiplier = "x" + threadBodies.size();
                bonus = "";
                //hyperthreading = "";
            }
        }
        //score_amount = lbl_score.toString() + score + "    x" + threadBodies.size();
        
        batch.begin();
        if(drawSprite){
            if (backgroundType == 1){
                batch.draw(background, bgx + 144*17, 0);
                batch.draw(background, bgx + 144*16, 0);
                batch.draw(background, bgx + 144*15, 0);
                batch.draw(background, bgx + 144*14, 0);
                batch.draw(background, bgx + 144*13, 0);
                batch.draw(background, bgx + 144*12, 0);
                batch.draw(background, bgx + 144*11, 0);
                batch.draw(background, bgx + 144*10, 0);
                batch.draw(background, bgx + 144*9, 0);
                batch.draw(background, bgx + 144*8, 0);
                batch.draw(background, bgx + 144*7, 0);
                batch.draw(background, bgx + 144*6, 0);
                batch.draw(background, bgx + 144*5, 0);            
                batch.draw(background, bgx + 144*4, 0);
                batch.draw(background, bgx + 144*3, 0);
                batch.draw(background, bgx + 144*2, 0);
                batch.draw(background, bgx + 144, 0);
                batch.draw(background, bgx, 0);
                batch.draw(background, bgx - 144, 0);

                batch.draw(background, bgx + 144*17, background.getHeight());
                batch.draw(background, bgx + 144*16, background.getHeight());
                batch.draw(background, bgx + 144*15, background.getHeight());
                batch.draw(background, bgx + 144*14, background.getHeight());
                batch.draw(background, bgx + 144*13, background.getHeight());
                batch.draw(background, bgx + 144*12, background.getHeight());
                batch.draw(background, bgx + 144*11, background.getHeight());
                batch.draw(background, bgx + 144*10, background.getHeight());
                batch.draw(background, bgx + 144*9, background.getHeight());
                batch.draw(background, bgx + 144*8, background.getHeight());
                batch.draw(background, bgx + 144*7, background.getHeight());
                batch.draw(background, bgx + 144*6, background.getHeight());
                batch.draw(background, bgx + 144*5, background.getHeight());            
                batch.draw(background, bgx + 144*4, background.getHeight());
                batch.draw(background, bgx + 144*3, background.getHeight());
                batch.draw(background, bgx + 144*2, background.getHeight());
                batch.draw(background, bgx + 144, background.getHeight());
                batch.draw(background, bgx, background.getHeight());
                batch.draw(background, bgx - 144, background.getHeight());

                batch.draw(colorBackground, bgcolorx, 0);
                batch.draw(colorBackground, bgcolorx, colorBackground.getHeight());

                batch.draw(colorBackground, bgcolorx - 2016, 0);
                batch.draw(colorBackground, bgcolorx - 2016, colorBackground.getHeight());

                batch.draw(threadedBackground, bgx + 144*17, 0);
                batch.draw(threadedBackground, bgx + 144*16, 0);
                batch.draw(threadedBackground, bgx + 144*15, 0);
                batch.draw(threadedBackground, bgx + 144*14, 0);
                batch.draw(threadedBackground, bgx + 144*13, 0);
                batch.draw(threadedBackground, bgx + 144*12, 0);
                batch.draw(threadedBackground, bgx + 144*11, 0);
                batch.draw(threadedBackground, bgx + 144*10, 0);
                batch.draw(threadedBackground, bgx + 144*9, 0);
                batch.draw(threadedBackground, bgx + 144*8, 0);
                batch.draw(threadedBackground, bgx + 144*7, 0);
                batch.draw(threadedBackground, bgx + 144*6, 0);
                batch.draw(threadedBackground, bgx + 144*5, 0);            
                batch.draw(threadedBackground, bgx + 144*4, 0);
                batch.draw(threadedBackground, bgx + 144*3, 0);
                batch.draw(threadedBackground, bgx + 144*2, 0);
                batch.draw(threadedBackground, bgx + 144, 0);
                batch.draw(threadedBackground, bgx, 0);
                batch.draw(threadedBackground, bgx - 144, 0);

                batch.draw(threadedBackground, bgx + 144*17, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*16, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*15, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*14, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*13, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*12, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*11, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*10, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*9, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*8, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*7, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*6, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*5, threadedBackground.getHeight());            
                batch.draw(threadedBackground, bgx + 144*4, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*3, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144*2, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx + 144, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx, threadedBackground.getHeight());
                batch.draw(threadedBackground, bgx - 144, threadedBackground.getHeight());

                batch.draw(shadowBackground, bgcolorx, 0);
                batch.draw(shadowBackground, bgcolorx - 2016, 0);

                batch.draw(shadowBackground, bgcolorx, shadowBackground.getHeight());
                batch.draw(shadowBackground, bgcolorx - 2016, shadowBackground.getHeight());
            } else if (backgroundType == 2){
                batch.draw(woodsBackground, 0, 0, width, height);
                
                batch.draw(woodsBack, bgx, 0);
                batch.draw(woodsBack, bgx - 1035, 0);
                batch.draw(woodsBack, bgx + 1035, 0);
                
                batch.draw(woodsFront, bgcolorx, 0);
                batch.draw(woodsFront, bgcolorx - 1035, 0);
                batch.draw(woodsFront, bgcolorx + 1035, 0);
                
                batch.draw(woodsClouds, bgcloudx - 1000 , height - woodsClouds.getHeight());
            } else if (backgroundType == 3 || backgroundType == 6){
                batch.draw(woodsBackground, 0, 0, width, height);
                
                batch.draw(woodsBack, bgx, 0);
                batch.draw(woodsBack, bgx - 1035, 0);
                batch.draw(woodsBack, bgx + 1035, 0);
                
                batch.draw(woodsFront, bgcolorx, 0);
                batch.draw(woodsFront, bgcolorx - 1035, 0);
                batch.draw(woodsFront, bgcolorx + 1035, 0);
                
                //batch.draw(woodsClouds, bgcloudx - 1400 , height - woodsClouds.getHeight());
            } else if (backgroundType == 4 || backgroundType == 5){
                batch.draw(woodsBackground, 0, 0, width, height);
                
                batch.draw(woodsBack, bgx, 0);
                batch.draw(woodsBack, bgx - 1035, 0);
                batch.draw(woodsBack, bgx + 1035, 0);
                
//                batch.draw(woodsFront, bgcolorx, 0);
//                batch.draw(woodsFront, bgcolorx - 1035, 0);
//                batch.draw(woodsFront, bgcolorx + 1035, 0);
                
                //batch.draw(woodsClouds, bgcloudx - 1400 , height - woodsClouds.getHeight());
            }
            
            
            batch.draw(player_sprite, player_sprite.getX(), player_sprite.getY(),player_sprite.getOriginX(),
                       player_sprite.getOriginY(), player_sprite.getWidth(),player_sprite.getHeight(),
                       player_sprite.getScaleX(),player_sprite.getScaleY(),player_sprite.getRotation());
            
            for (Sprite scissors_sprite : scissorSprites){
                batch.draw(scissors_sprite, scissors_sprite.getX(), scissors_sprite.getY(),scissors_sprite.getOriginX(),
                           scissors_sprite.getOriginY(), scissors_sprite.getWidth(),scissors_sprite.getHeight(),
                           scissors_sprite.getScaleX(),scissors_sprite.getScaleY(),scissors_sprite.getRotation());
            }
            
            for (Sprite needle_sprite : needleSprites){
                batch.draw(needle_sprite, needle_sprite.getX(), needle_sprite.getY(),needle_sprite.getOriginX(),
                           needle_sprite.getOriginY(), needle_sprite.getWidth(),needle_sprite.getHeight(),
                           needle_sprite.getScaleX(),needle_sprite.getScaleY(),needle_sprite.getRotation());
            }
            
            for (Sprite threadSprite : threadSprites) {
                batch.draw(threadSprite, threadSprite.getX(), threadSprite.getY(), threadSprite.getOriginX(), 
                        threadSprite.getOriginY(), threadSprite.getWidth(), threadSprite.getHeight(), 
                        threadSprite.getScaleX(), threadSprite.getScaleY(), threadSprite.getRotation());
            }
            
            for (Sprite particleSprite : particleSprites){
                batch.draw(particleSprite, particleSprite.getX(), particleSprite.getY(), particleSprite.getOriginX(), 
                        particleSprite.getOriginY(), particleSprite.getWidth(), particleSprite.getHeight(), 
                        particleSprite.getScaleX(), particleSprite.getScaleY(), particleSprite.getRotation());
            }
            if (backgroundType == 2 || backgroundType == 3){
                font = blackfont;
            }
            if (drawText){
                font.draw(batch, score_amount, 0, 50);
                font.draw(batch, multiplier, 500, 50);
                font.draw(batch, bonus, 500, 50);
                font.draw(batch, hyperthreading, 0, height);
                font.draw(batch, songTitle, 0, height);
            }
        }

        batch.end();
        if (drawBoxes){
            debugRenderer.render(world, debugMatrix);
        }
        
    }

    public void endLevel(){
        List<NameValuePair> parameters = new ArrayList<NameValuePair>(1);
        parameters.add(new BasicNameValuePair("uid", Installation.id()));
        parameters.add(new BasicNameValuePair("needles_thread", Integer.toString(this.needles_thread)));
        parameters.add(new BasicNameValuePair("thread_cut", Integer.toString(this.thread_cut)));
        parameters.add(new BasicNameValuePair("songs_played", "1"));
        parameters.add(new BasicNameValuePair("beats", Integer.toString(this.beats)));
        parameters.add(new BasicNameValuePair("total_needles", Integer.toString(this.needles)));
        
        System.out.println("Threaded: "+needles_thread);
        
        db.updateUser(parameters);
        
        Results results = new Results();
        ArrayList<String> res = new ArrayList<String>();
        res.add((int)((this.needles_thread/(double)this.needles)*100) + "% thread rate");
        results.setResults(res);
        
        System.out.println("-------------------------------");
        System.out.println(" Difficulty: " + difficulty);
        System.out.println("    Needles: " + needles);
        System.out.println("   Scissors: " + scissors);
        System.out.println("-------------------------------");
        
        FinishScreen finish = new FinishScreen(tss, lbl_score.toString() + score, track, results, (String)titleDisplay,"By: " + (String)songArtist, difficulty);
        tss.setScreen(finish);
    }
    
    @Override
    public void hide() {
        dispose();
    }

}