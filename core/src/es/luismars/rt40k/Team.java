package es.luismars.rt40k;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luism on 02/12/2017.
 */
public class Team extends Group {

    Vector2 goal = new Vector2();
    Vector2 center = new Vector2();
    Vector2 shootingDirection = new Vector2();
    Vector2 ray = new Vector2();

    List<TeamMember> members = new ArrayList<>();
    List<Body> bullets = new ArrayList<>();
    private World world;
    private float baseRadius;
    float distanceRadius;
    private boolean teamA;
    static TextureRegion bulletImg = new TextureRegion(new Texture("bullet.png"));
    static TextureRegion targetImg = new TextureRegion(new Texture("marker.png"));
    static TextureRegion destinationImg = new TextureRegion(new Texture("position_marker.png"));

    float shootingCooldown = 3;
    public Team(World world, float baseRadius, boolean teamA) {
        this.world = world;
        this.baseRadius = baseRadius;
        this.distanceRadius = baseRadius * 2f;
        this.teamA = teamA;
    }

    @Override
    public void act(float delta) {
        shootingCooldown -= delta;

        center.setZero();
        members.removeIf(TeamMember::isDead);


        for (TeamMember member : members) {
            center.add(member.body.getPosition());
            member.body.getFixtureList().get(1).getShape().setRadius(distanceRadius);
        }

        center.scl(1f / members.size());

        if (center.cpy().sub(goal).len() > 1) {
            for (TeamMember member : members) {
                member.reactToTeam(goal, center);
            }
        } else {
            for (TeamMember member : members) {
                member.stop();
            }
        }
        super.act(delta);
//        for (TeamMember member : members) {
//            member.act(delta);
//        }

    }

    public void addMember(Vector2 position) {
        TeamMember member = new TeamMember(world, position, baseRadius, teamA, this);
        members.add(member);
        addActor(member);
    }

    public void addDistanceRadius(int amount) {
        distanceRadius = MathUtils.clamp(distanceRadius + amount / 10f, baseRadius, 1.5f + baseRadius);
    }

    public void shootAt() {
        for (TeamMember member : members) {
            member.shoot();
        }
    }

    public void setShootingDirection(Vector2 pos) {
        shootingDirection.set(pos);
        for (TeamMember member : members) {
            member.shootingDirection = pos;
        }
    }

    public boolean isEnemyTeamVisible(List<TeamMember> enemyMembers) {
        if (shootingCooldown <= 0) {
            shootingCooldown = 3;
        } else {
            return false;
        }
        boolean isVisible = false;
        for (TeamMember member : members) {
            TeamMember closestVisible = null;
            for (TeamMember teamMember : enemyMembers) {

                final float[] closestDist = {10};
                final TeamMember[] closestMember = {null};
                world.rayCast((fixture, point, normal, fraction) -> {

                    if (fraction < closestDist[0]) {
                        Object userData = fixture.getUserData();
                        if (userData.equals("Wall")) {
                            closestDist[0] = fraction;
                            closestMember[0] = null;
                        } else if (userData instanceof TeamMember && ((TeamMember) userData).teamA != teamA) {
                            closestDist[0] = fraction;
                            closestMember[0] = ((TeamMember) userData);

                        }
                    }
                    return 1;
                }, member.body.getPosition(), teamMember.body.getPosition());
                if (closestMember[0] != null) {
                    closestVisible = closestMember[0];
                }
            }
            if (closestVisible != null) {
                isVisible = true;
                member.shootingDirection = closestVisible.body.getPosition();
                member.waitingToShoot = true;
            }
        }

        return isVisible;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        for (TeamMember member : members) {
            member.drawTop(batch, parentAlpha);
        }
        for (Body bullet : bullets) {
            batch.draw(bulletImg, bullet.getPosition().x - 0.1f, bullet.getPosition().y - 0.1f, 0, 0, 0.2f, 0.2f, 1, 1, bullet.getLinearVelocity().angle());
        }
    }

    public boolean allDead() {
        for (TeamMember member : members) {
            if (!member.isDead()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);
        shapes.line(center, ray);
    }

    public void drawTarget(Batch batch) {
        batch.draw(targetImg, shootingDirection.x - 1, shootingDirection.y - 1, 2, 2);
        batch.draw(destinationImg, goal.x - 1, goal.y - 1, 2, 2);
    }
}
