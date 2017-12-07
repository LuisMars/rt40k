package es.luismars.rt40k;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static es.luismars.rt40k.BoardStage.HEIGHT;
import static es.luismars.rt40k.BoardStage.WIDTH;

/**
 * Created by luism on 06/12/2017.
 */
public class HudStage extends Stage {

    private final Main main;
    private final BoardStage stage;

    TextButton autoShootButton;

    public HudStage(Main main, BoardStage stage) {
        super(new ExtendViewport(WIDTH * 10, HEIGHT * 10));
        this.main = main;
        this.stage = stage;

        Skin skin = new Skin(Gdx.files.internal("kenny_gui/skin.json"));
        Table table = new Table(skin);
        addActor(table);
        table.setFillParent(true);
        table.top().left();
        autoShootButton = new TextButton("Auto Shoot: Off", skin);


        autoShootButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (autoShootButton.getClickListener().isVisualPressed()) {
                    stage.autoShoot = !stage.autoShoot;
                    changeAutoShoot();
                }
            }
        });
        table.add(autoShootButton);

    }

    public void changeAutoShoot() {
        autoShootButton.setChecked(stage.autoShoot);
        if (stage.autoShoot) {
            autoShootButton.setText("Auto Shoot: On");
        } else {

            autoShootButton.setText("Auto Shoot: Off");
        }
    }

    public boolean hit(int screenX, int screenY) {
        Vector2 coordinates = screenToStageCoordinates(new Vector2(screenX, screenY));
        return hit(coordinates.x, coordinates.y, true) != null;
    }
}
