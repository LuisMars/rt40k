package es.luismars.rt40k;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luism on 03/12/2017.
 */
public class BodyCreator {
    final static short TEAM_A = 0x0001;
    final static short TEAM_A_BULLET = 0x0002;
    final static short TEAM_A_BASE = 0x0004;
    final static short TEAM_B = 0x0008;
    final static short TEAM_B_BULLET = 0x0010;
    final static short TEAM_B_BASE = 0x0012;
    final static short WALL = 0x0014;

    private static BodyCreator ourInstance = new BodyCreator();

    private static Map<Float, FixtureDef> fixturesA = new HashMap<>();
    private static Map<Float, FixtureDef> fixturesB = new HashMap<>();

    public static BodyCreator getInstance() {
        return ourInstance;
    }

    public static Body createBullet(World world, Vector2 pos, boolean teamA) {
        short category = teamA ? TEAM_A_BULLET : TEAM_B_BULLET;

        short mask = ~(WALL | TEAM_A_BASE | TEAM_B_BASE); //(short) (teamA ? TEAM_B : TEAM_A);
        return createBall(world, "Bullet", pos.x, pos.y, 0.1f, teamA, category, mask, false);
    }

    public static Body createMember(World world, Object obj, float x, float y, float radius, boolean teamA) {
        short category = teamA ? TEAM_A : TEAM_B;

        short mask = (short) (~(teamA ? TEAM_A_BULLET : TEAM_B_BULLET));

        return createBall(world, obj, x, y, radius, teamA, category, mask, true);
    }

    public static Body createBall(World world, Object obj, float x, float y, float radius, boolean teamA, short category, short mask, boolean base) {
        Map<Float, FixtureDef> fixtures;
        if (teamA) {
            fixtures = BodyCreator.fixturesA;
        } else {
            fixtures = BodyCreator.fixturesB;
        }
        if (!fixtures.containsKey(radius)) {
            FixtureDef fixtureDef = new FixtureDef();
            CircleShape circle = new CircleShape();
            circle.setRadius(radius);
            fixtureDef.shape = circle;
            if (base) {
                fixtureDef.density = 5f;
            } else {
                fixtureDef.density = 10f;
            }
            fixtureDef.friction = 0f;
            fixtureDef.restitution = 0.1f; // Make it bounce a little bit
            fixtureDef.filter.categoryBits = category;
            fixtureDef.filter.maskBits = mask;
            fixtures.put(radius, fixtureDef);
        }
        FixtureDef baseFixtureDef = new FixtureDef();
        if (base) {
            CircleShape circle = new CircleShape();
            circle.setRadius(radius * 2f);
            baseFixtureDef.shape = circle;
            baseFixtureDef.density = 0f;
            baseFixtureDef.friction = 0f;
            baseFixtureDef.restitution = 0f; // Make it bounce a little bit
            baseFixtureDef.filter.categoryBits = teamA ? TEAM_A_BASE : TEAM_B_BASE;
            baseFixtureDef.filter.maskBits = teamA ? TEAM_A_BASE : TEAM_B_BASE;
        }
        FixtureDef fixtureDef = fixtures.get(radius);


        BodyDef bodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the world
        bodyDef.position.set(x, y);

        // Create our body in the world using our body definition
        Body body = world.createBody(bodyDef);
        // Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(obj);
        if (base) {
            body.createFixture(baseFixtureDef).setUserData("Base");
        }

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        body.setFixedRotation(true);
        return body;
    }

    public static Body createBuilding(World world, float x, float y, float width, float height) {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(x, y));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width, height);
        groundBody.createFixture(groundBox, 0.0f).setUserData("Wall");
        groundBody.getFixtureList().first().getFilterData().categoryBits = WALL;
        
        return groundBody;
    }

    public static Body createWalls(World world, float width, float height) {

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(0, -1));
        Body groundBody = world.createBody(groundBodyDef);
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width, 1);
        groundBody.createFixture(groundBox, 0.0f).setUserData("Wall");
        groundBody.getFixtureList().first().getFilterData().categoryBits = WALL;

        // Clean up after ourselves
        groundBox.dispose();

        groundBodyDef = new BodyDef();

        groundBodyDef.position.set(new Vector2(0, height + 1));
        groundBody = world.createBody(groundBodyDef);

        groundBox = new PolygonShape();
        groundBox.setAsBox(width, 1);
        groundBody.createFixture(groundBox, 0.0f).setUserData("Wall");
        groundBody.getFixtureList().first().getFilterData().categoryBits = WALL;

        // Clean up after ourselves
        groundBox.dispose();

        groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(0, 0));
        groundBody = world.createBody(groundBodyDef);

        groundBox = new PolygonShape();
        groundBox.setAsBox(0, height);
        groundBody.createFixture(groundBox, 0.0f).setUserData("Wall");
        groundBody.getFixtureList().first().getFilterData().categoryBits = WALL;

        // Clean up after ourselves
        groundBox.dispose();

        groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(width, 0));
        groundBody = world.createBody(groundBodyDef);

        groundBox = new PolygonShape();
        groundBox.setAsBox(0, height);
        groundBody.createFixture(groundBox, 0.0f).setUserData("Wall");
        groundBody.getFixtureList().first().getFilterData().categoryBits = WALL;

        // Clean up after ourselves
        groundBox.dispose();
        return groundBody;
    }
    public BodyCreator() {
        // Create a circle shape and set its radius to 6
    }
}
