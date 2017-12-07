package es.luismars.rt40k;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * Created by luism on 05/12/2017.
 */
public class BoardTile extends Actor {

//    static Texture diffuse = new Texture(Gdx.files.internal("dirt.jpg"));
    static TextureRegion[][] tileset = new TextureRegion(new Texture(Gdx.files.internal("tileset.png"))).split(512, 512);

    private final float x;
    private final float y;
    int i;
    int j;

    public BoardTile(float x, float y) {
        this(x, y, 0, 0);
    }

    public BoardTile(float x, float y, int i, int j) {
        this.x = x;
        this.y = y;
        this.i = i;
        this.j = j;
        setBounds(x, y, 4, 4);
        setTouchable(Touchable.enabled);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(tileset[i][j], x, y, 4, 4);
    }



}
