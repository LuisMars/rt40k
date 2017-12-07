package es.luismars.rt40k;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import java.util.List;

/**
 * Created by luism on 03/12/2017.
 */
public class BoardScreen implements Screen {
    private Main main;
    private BoardStage stage;
    private HudStage hudStage;

    public BoardScreen(Main main, List<BoardTile> tiles) {
        this.main = main;
        stage = new BoardStage(main);
        hudStage = new HudStage(main, stage);
        stage.hudStage = hudStage;

        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, hudStage);

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

//        Gdx.gl.glClearColor(0, 0, 0, 1);
        stage.act();
        hudStage.act();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        hudStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        hudStage.getViewport().update(width, height, true);
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
