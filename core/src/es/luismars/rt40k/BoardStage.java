package es.luismars.rt40k;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by luism on 03/12/2017.
 */
public class BoardStage extends Stage {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Main main;
    HudStage hudStage;
    static float WIDTH = 72;

    static float HEIGHT = 48;
    Team myTeam;

    List<Team> armyA;
    List<Team> armyB;
    Set<Body> toRemove = new HashSet<Body>();

    World world;
    Box2DDebugRenderer debugRenderer;

    List<BoardTile> tiles = new ArrayList<>();

    List<TeamMember> allEnemies = new ArrayList<>();

    boolean autoShoot = false;

    public BoardStage(Main main) {
        super(new ExtendViewport(WIDTH, HEIGHT));
        this.main = main;

        world = new World(new Vector2(0, 0), true);


        armyA = new ArrayList<>();
        armyB = new ArrayList<>();

        tiledMap = new TmxMapLoader().load("tiled_test.tmx");
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        for (int x = 0; x < WIDTH / 4; x++) {
            for (int y = 0; y < HEIGHT / 4; y++) {
                int id = tileLayer.getCell(x, y).getTile().getId() - 1;
                if (id != 0) {
                    BodyCreator.createBuilding(world, 2 + x * 4, 2 + y * 4, 2, 2);
                }
            }
        }
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 4 / 512f, getBatch());

        MapLayer mapLayer = tiledMap.getLayers().get(1);
        for (MapObject mapObject : mapLayer.getObjects()) {
            MapProperties properties = mapObject.getProperties();
            if (properties.get("type").equals("Player")) {
                int n = Integer.parseInt(mapObject.getName());


                myTeam = new Team(world, 0.5f, true);
                float minX = (float) properties.get("x") * 4 / 512f;
                float maxX = (float) properties.get("width") * 4  / 512f + minX;

                float minY = (float) properties.get("y") * 4  / 512f;
                float maxY = (float) properties.get("height") * 4  / 512f + minY;

                for (int i = 0; i < n; i++) {
                    myTeam.addMember(new Vector2(MathUtils.random(minX, maxX), MathUtils.random(minY, maxY)));
                }
                myTeam.goal.set((minX + maxX) / 2, (minY + maxY) / 2);
                armyA.add(myTeam);
                addActor(myTeam);
            } else if (properties.get("type").equals("Enemy")) {
                int n = Integer.parseInt(mapObject.getName());

                Team teamB = new Team(world, 0.5f, false);

                float minX = (float) properties.get("x") * 4 / 512f;
                float maxX = (float) properties.get("width") * 4  / 512f + minX;

                float minY = (float) properties.get("y") * 4  / 512f;
                float maxY = (float) properties.get("height") * 4  / 512f + minY;

                for (int i = 0; i < n; i++) {
                    teamB.addMember(new Vector2(MathUtils.random(minX, maxX), MathUtils.random(minY, maxY)));
                }
                teamB.goal.set((minX + maxX) / 2, (minY + maxY) / 2);
                armyB.add(teamB);
                addActor(teamB);
            }
        }




        debugRenderer = new Box2DDebugRenderer();

        BodyCreator.createWalls(world, 72, 48);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();
                Object userDataA = contact.getFixtureA().getUserData();
                Object userDataB = contact.getFixtureB().getUserData();
                if (userDataA != null && userDataA.equals("Bullet")) {
                    if (!toRemove.contains(bodyA)) {
                        toRemove.add(bodyA);
                        if (userDataB != null && userDataB.getClass() == TeamMember.class) {
                            TeamMember teamMember = (TeamMember) userDataB;
                            if (teamMember.wound()) {
                                toRemove.add(bodyB);
                            }
                        }
                    }
                }

                if (userDataB != null && userDataB.equals("Bullet")) {
                    if (!toRemove.contains(bodyB)) {
                        toRemove.add(bodyB);
                        if (userDataA != null && userDataA.getClass() == TeamMember.class) {
                            TeamMember teamMember = (TeamMember) userDataA;
                            if (teamMember.wound()) {
                                toRemove.add(bodyA);
                            }
                        }
                    }
                }

            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (hudStage.hit(screenX, screenY)) {
            return false;
        }
        if (button == 0) {
            myTeam.goal = screenToStageCoordinates(new Vector2(screenX, screenY));
        } else if (button == 1) {
            myTeam.setShootingDirection(screenToStageCoordinates(new Vector2(screenX, screenY)));
            myTeam.shootAt();
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (hudStage.hit(screenX, screenY)) {
            return false;
        }
        if (Gdx.input.isButtonPressed(0)) {
            myTeam.goal = screenToStageCoordinates(new Vector2(screenX, screenY));
        } else if (Gdx.input.isButtonPressed(1)){
            myTeam.setShootingDirection(screenToStageCoordinates(new Vector2(screenX, screenY)));
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            myTeam.addDistanceRadius(-amount);
        } else {
            float zoom = ((OrthographicCamera) getCamera()).zoom;
            System.out.println(zoom);
            ((OrthographicCamera) getCamera()).zoom = MathUtils.clamp(zoom + amount / 20f, 0.1f, 1);

        }
        return true;
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch (keyCode) {
            case Input.Keys.SPACE:
                autoShoot = !autoShoot;
                hudStage.changeAutoShoot();
                return true;
            default:
                return false;
        }

    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Vector3 newCamPos = new Vector3();
        float zoom = ((OrthographicCamera) getCamera()).zoom;
        if (MathUtils.isEqual(zoom, 1)) {
            newCamPos.set(WIDTH / 2f, HEIGHT / 2f, 0);
        } else {
            float nX = Math.min(Math.max(WIDTH * zoom / 2f, myTeam.center.x), WIDTH - (WIDTH * zoom / 2f));
            float nY = Math.min(Math.max(HEIGHT * zoom / 2f, myTeam.center.y), HEIGHT - (HEIGHT * zoom / 2f));

            newCamPos.set(nX, nY, 0);
        }

        getCamera().position.interpolate(newCamPos, 0.5f, Interpolation.linear);


        allEnemies.clear();

        for (Team team : armyB) {
            if (team.allDead()) {
                continue;
            }
            if (team.isEnemyTeamVisible(myTeam.members)) {
//                team.setShootingDirection(myTeam.center);
                team.shootAt();
            }
            allEnemies.addAll(team.members);
        }

        allEnemies.removeIf(TeamMember::isDead);

        if (autoShoot && myTeam.isEnemyTeamVisible(allEnemies)) {
            myTeam.shootAt();
        }
        world.step(delta, 60, 20);

        for (Body body : toRemove) {
            world.destroyBody(body);
            for (Team team : armyA) {
                team.bullets.remove(body);
            }
            for (Team team : armyB) {
                team.bullets.remove(body);
            }
        }

        toRemove.clear();
    }

    @Override
    public void draw() {

        tiledMapRenderer.setView((OrthographicCamera) getCamera());
        tiledMapRenderer.render();
        getBatch().begin();

        getBatch().setProjectionMatrix(getCamera().combined);
        myTeam.drawTarget(getBatch());
        getBatch().end();
        super.draw();

        if (isDebugAll()) {
            debugRenderer.render(world, getCamera().combined);
        }

    }
}
