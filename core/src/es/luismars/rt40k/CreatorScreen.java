package es.luismars.rt40k;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by luism on 03/12/2017.
 */
public class CreatorScreen implements Screen {
    private Main main;
    //    private BoardStage stage;
    private CreatorStage stage;
//    private ShaderStage stage;


    public CreatorScreen(Main main) {
        this.main = main;
        stage = new CreatorStage(main);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

//        Gdx.gl.glClearColor(0, 0, 0, 1);
        stage.act();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
