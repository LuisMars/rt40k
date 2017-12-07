package es.luismars.rt40k;

import com.badlogic.gdx.Game;

public class Main extends Game {


	@Override
	public void create() {
		setScreen(new BoardScreen(this,null));
	}
}
