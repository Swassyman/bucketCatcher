package pong.PongJDK;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Sound dropSound;
    Music bgMusic;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Sprite bucketSprite;
    Vector2 touchPos;
    Array<Sprite> dropSprites;
    float dropTimer;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;

    @Override
    public void create() { //initialises all variables
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8,5);
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1,1);
        touchPos = new Vector2();
        dropSprites = new Array<>();
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        bgMusic.setLooping(true);
        bgMusic.setVolume(0.3f);
        bgMusic.play();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true); //updates viewport when user resizes window
    }

    @Override
    public void render() { //updates each frame
        input();
        logic();
        draw();
    }

    private void input(){
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime(); //time difference between frames, help consistency over various frame rates

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            bucketSprite.translateX(speed*delta);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            bucketSprite.translateX(-speed*delta);
        }
        else if(Gdx.input.isTouched()){
            touchPos.set(Gdx.input.getX(), Gdx.input.getY()); //sets position of touch
            viewport.unproject(touchPos); //converts window position to world units
            bucketSprite.setCenterX(touchPos.x); // changes the position of sprite to touch
        }
    }

    private void logic(){
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();
        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth-bucketWidth));
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        float delta = Gdx.graphics.getDeltaTime();

        //removes sprite from array once it goes below view
        //saves memory and prevents crashing on long sessions
        for(int i = dropSprites.size-1; i>-1; i--){
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);
            dropSprite.translateY(-2.8f * delta);

            if(dropSprite.getY() < -worldHeight) dropSprites.removeIndex(i);
            else if(bucketRectangle.overlaps(dropRectangle)){ //checks for collision among the two
                dropSprites.removeIndex(i);
                dropSound.play();
            }
        }
        dropTimer += delta;
        if(dropTimer>1f){
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        bucketSprite.draw(spriteBatch);

        for(Sprite dropSprite: dropSprites){
            dropSprite.draw(spriteBatch);
        }
        spriteBatch.end();
    }

    private void createDroplet(){
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth-dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }
    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
