package es.luismars.rt40k;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.List;

import static es.luismars.rt40k.BoardStage.HEIGHT;
import static es.luismars.rt40k.BoardStage.WIDTH;

/**
 * Created by luism on 06/12/2017.
 */
public class CreatorStage extends Stage {

    private final Main main;
    private final List<BoardTile> tiles = new ArrayList<>();

    public CreatorStage(Main main) {
        super(new ExtendViewport(WIDTH, HEIGHT));
        this.main = main;
        for (int x = 0; x < WIDTH; x += 4) {
            for (int y = 0; y < HEIGHT; y += 4) {
                BoardTile boardTile = new BoardTile(x, y);
                addActor(boardTile);
                tiles.add(boardTile);
            }
        }
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean keyDown(int keyCode) {
        main.setScreen(new BoardScreen(main, tiles));
        this.dispose();
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 stageCoordinates = screenToStageCoordinates(new Vector2(screenX, screenY));
        BoardTile tile = ((BoardTile) hit(stageCoordinates.x, stageCoordinates.y, true));
        if (button == 0) {
            tile.i++;
            tile.i %= 4;
        } else {
            tile.j++;
            tile.j %= 4;
        }
        return true;
    }
}
