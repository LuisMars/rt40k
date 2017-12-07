package es.luismars.rt40k;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by luism on 02/12/2017.
 */
public class TeamMember extends Actor {

    private final World world;
    private final Vector2 position;
    final boolean teamA;
    private Team team;
    float maxSpeed = 6;
    float maxAcc = 6f;
    Body body, spacing;
    float baseRadius, distanceRadius;

    private int wounds = 1;
    float accuracy = 2f;


    private float shootingTimer = 0;
    Vector2 shootingDirection;
    boolean waitingToShoot = false;
    boolean timerSet = false;

    Sound sound = Gdx.audio.newSound(Gdx.files.internal("shots/pistol.wav"));
//    Sound sound = Gdx.audio.newSound(Gdx.files.internal("shot_1_16.wav"));
    long lastSoundId;

    static TextureRegion soldierRed = new TextureRegion(new Texture("soldier.png"));
    static TextureRegion soldierRedTop = new TextureRegion(new Texture("soldier_top.png"));

    static TextureRegion soldierBlue = new TextureRegion(new Texture("soldier_blue.png"));
    static TextureRegion soldierBlueTop = new TextureRegion(new Texture("soldier_blue_top.png"));

    TextureRegion img, imgTop;

    float angle = 0;

    public TeamMember(World world, Vector2 position, float baseRadius, boolean teamA, Team team) {
        this.world = world;
        this.position = position;
        this.teamA = teamA;
        this.team = team;

        body = BodyCreator.createMember(world, this, position.x, position.y, baseRadius, teamA);

        this.baseRadius = baseRadius;
        this.distanceRadius = baseRadius;

        if (teamA) {
            img = soldierBlue;
            imgTop = soldierBlueTop;
        } else {
            img = soldierRed;
            imgTop = soldierRedTop;
        }
    }

    @Override
    public void act(float delta) {
        if (waitingToShoot) {
            shootingTimer -= delta;
            if (shootingTimer <= 0 && shootingDirection != null) {
                Vector2 sub = shootingDirection.cpy().sub(body.getPosition());

                float accuracyDegrees;
                if (MathUtils.randomBoolean(0.5f)) {
                    accuracyDegrees = sub.len() / accuracy;
                } else {
                    accuracyDegrees = 0;
                }


                Vector2 direction = sub.setLength(120);
                Vector2 memberPos = body.getPosition();
                Body bullet = BodyCreator.createBullet(world, memberPos, teamA);
                bullet.setBullet(true);
                bullet.applyLinearImpulse(direction.rotate(MathUtils.random(-accuracyDegrees, accuracyDegrees)), Vector2.Zero, true);
                team.bullets.add(bullet);
                sound.stop(lastSoundId);
                lastSoundId = sound.play(0.125f, MathUtils.random(0.8f, 1.2f), 0);
                waitingToShoot = false;
                timerSet = false;
            }
        }
        if (isDead()) {
            remove();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!body.getLinearVelocity().isZero()) {
            angle = angle * 0.95f + body.getLinearVelocity().angle() * 0.05f;
        }
        batch.draw(img, body.getPosition().x - 1, body.getPosition().y - 1, 1, 1, 2, 2, 1, 1, angle);
    }
    public void drawTop(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!body.getLinearVelocity().isZero()) {
            angle = angle * 0.95f + body.getLinearVelocity().angle() * 0.05f;
        }
        batch.draw(imgTop, body.getPosition().x - 1, body.getPosition().y - 1, 1, 1, 2, 2, 1, 1, angle);
    }

    public void reactToTeam(Vector2 goal, Vector2 center) {
        Vector2 middleAcc;

        middleAcc = goal.cpy().add(center).scl(0.5f).sub(body.getPosition());

        middleAcc.clamp(-maxAcc, maxAcc);
        body.applyLinearImpulse(middleAcc, Vector2.Zero, true);
        body.setLinearVelocity(body.getLinearVelocity().clamp(-maxSpeed, maxSpeed));

    }

    public void shoot() {
        if (waitingToShoot && !timerSet) {
            shootingTimer = MathUtils.random(0.5f);
        }
    }

    public void stop() {
        if (body.getLinearVelocity().isZero(6)) {
            body.setLinearVelocity(body.getLinearVelocity().scl(0.5f));
            return;
        }

//        body.applyForceToCenter(body.getLinearVelocity().scl(-1f), true);
        body.setLinearVelocity(body.getLinearVelocity().scl(0.9f));
    }

    public boolean wound() {
        if (MathUtils.randomBoolean(0.5f)) {
            wounds--;
        }
        return isDead();
    }

    public boolean isDead() {
        return wounds <= 0;
    }
}
