package testes;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.Iterator;

/**
 * Example 12 - how to give objects physical properties so they bounce and fall.
 *
 * @author base code by double1984, updated by zathras
 * Teste de commit
 */
public class TesteCanhao extends SimpleApplication {

    public static void main(String args[]) {
        TesteCanhao app = new TesteCanhao();
        app.start();
    }
    /**
     * Prepare the Physics Application State (jBullet)
     */
    private BulletAppState bulletAppState;
    /**
     * Prepare Materials
     */
    Material wall_mat;
    Material stone_mat;
    Material floor_mat;
    /**
     * Prepare geometries and physical nodes for bricks and cannon balls.
     */
    private RigidBodyControl brick_phy;
    private static final Box box;
    private RigidBodyControl ball_phy;
    private static final Sphere sphere;
    private RigidBodyControl floor_phy;
    private static final Box floor;
    /**
     * dimensions used for bricks and wall
     */
    private static final float brickLength = 0.48f;
    private static final float brickWidth = 0.24f;
    private static final float brickHeight = 0.12f;
    private Boolean debugFisica = true;

    static {
        /**
         * Initialize the cannon ball geometry
         */
        sphere = new Sphere(32, 32, 0.4f, true, false);
        sphere.setTextureMode(TextureMode.Projected);
        /**
         * Initialize the brick geometry
         */
        box = new Box(brickLength, brickHeight, brickWidth);
        box.scaleTextureCoordinates(new Vector2f(1f, .5f));
        /**
         * Initialize the floor geometry
         */
        floor = new Box(10f, 0.1f, 5f);
        floor.scaleTextureCoordinates(new Vector2f(3, 6));
    }

    @Override
    public void simpleInitApp() {
        /**
         * Set up Physics Game
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
//        bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        /**
         * Configure cam to look at scene
         */
//    cam.setLocation(new Vector3f(0, 4f, 36f));
//    cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
//    Posicao da camera: -8.588583 2.7286167 3.574437
//    Direcao camera: 0.93333024 -0.0631834 -0.3534156
        cam.setLocation(new Vector3f(-8.588583f, 2.7286167f, 3.574437f));
        cam.lookAt(new Vector3f(0.93333024f, -0.0631834f, -0.3534156f), Vector3f.UNIT_Y);

        /**
         * Add InputManager action: Left click triggers shooting.
         */
        inputManager.addMapping("shoot",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");

        /**
         * Add InputManager action: R key resets scene.
         */
        inputManager.addMapping("resetarCena",
                new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(actionListener, "resetarCena");
        /**
         * Add InputManager action: T key resets scene.
         */
        inputManager.addMapping("resetarCenaManterGeometrias",
                new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "resetarCenaManterGeometrias");
        
        /**
         * Add InputManager action: D key activate collision debug.
         */
        inputManager.addMapping("debugFisica",
                new KeyTrigger(KeyInput.KEY_B));
        inputManager.addListener(actionListener, "debugFisica");
        
        /**
         * Initialize the scene, materials, and physics space
         */
        initMaterials();
        initWall();
        initFloor();
        initCrossHairs();
    }
    /**
     * Every time the shoot action is triggered, a new cannon ball is produced.
     * The ball is set up to fly from the camera position in the camera
     * direction.
     */
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            bulletAppState.isEnabled();
            if (name.equals("shoot") && !keyPressed) {
                System.out.println(bulletAppState.isEnabled());
                makeCannonBall();
                System.out.println("Posicao da camera: " + cam.getLocation().x + " " + cam.getLocation().y + " " + cam.getLocation().z);
                System.out.println("Direcao camera: " + cam.getDirection().x + " " + cam.getDirection().y + " " + cam.getDirection().z);
            }

            if (name.equals("resetarCenaManterGeometrias") && keyPressed) {
                resetarCenaManterGeometrias();
            }
            if (name.equals("resetarCena")) {
                resetarCena();
            }
        }
    };

    /**
     * Initialize the materials used in this scene.
     */
    public void initMaterials() {
        wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        wall_mat.setTexture("ColorMap", tex);

        stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        stone_mat.setTexture("ColorMap", tex2);

        floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
    }

    /**
     * Make a solid floor and add it to the scene.
     */
    public void initFloor() {
        Geometry floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, -0.1f, 0);
        this.rootNode.attachChild(floor_geo);
        /* Make the floor physical with mass 0.0f! */
        floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);
    }

    /**
     * This loop builds a wall out of individual bricks.
     */
    public void initWall() {
        for (int k = 0; k < 1; k++) {
            float startpt = brickLength / 4;
            float height = 0;
            for (int j = 0; j < 15; j++) {
                for (int i = 0; i < 6; i++) {
                    Vector3f vt =
                            new Vector3f(i * brickLength * 2 + startpt, brickHeight + height, k * brickWidth * 2);
                    makeBrick(vt);
                }
                startpt = -startpt;
                height += 2 * brickHeight;
            }
        }
    }

    /**
     * This method creates one individual physical brick.
     */
    public void makeBrick(Vector3f loc) {
        /**
         * Create a brick geometry and attach to scene graph.
         */
        Geometry brick_geo = new Geometry("brick", box);
        brick_geo.setMaterial(wall_mat);
        rootNode.attachChild(brick_geo);
        /**
         * Position the brick geometry
         */
        brick_geo.setLocalTranslation(loc);
        /**
         * Make brick physical with a mass > 0.0f.
         */
        brick_phy = new RigidBodyControl(2f);
        /**
         * Add physical brick to physics space.
         */
        brick_geo.addControl(brick_phy);
        bulletAppState.getPhysicsSpace().add(brick_phy);
        brick_phy.setFriction(1.0f);
        brick_phy.setDamping(0.0f, 0.0f);
        brick_phy.setRestitution(0.0f);
        brick_phy.setSleepingThresholds(1.0f, 1.0f);
    }

    /**
     * Este metodo reseta os bricks da cena.
     */
    public void resetarCenaManterGeometrias() {
        bulletAppState.getPhysicsSpace().clearForces();
        bulletAppState.getPhysicsSpace().destroy();
        bulletAppState.getPhysicsSpace().create();
        initWall();
        initFloor();
    }
    
    /**
     * Este metodo reseta os bricks da cena.
     */
    public void resetarCena() {
        for (Iterator<Spatial> it = rootNode.getChildren().iterator(); it.hasNext();) {
            Spatial spat = it.next();
            rootNode.detachChild(spat);
        }
        bulletAppState.getPhysicsSpace().clearForces();
        bulletAppState.getPhysicsSpace().destroy();
        bulletAppState.getPhysicsSpace().create();
        initWall();
        initFloor();
    }

    /**
     * This method creates one individual physical cannon ball. By defaul, the
     * ball is accelerated and flies from the camera position in the camera direction.
     */
    public void makeCannonBall() {
        /**
         * Create a cannon ball geometry and attach to scene graph.
         */
        Geometry ball_geo = new Geometry("cannon ball", sphere);
        ball_geo.setMaterial(stone_mat);
        rootNode.attachChild(ball_geo);

//    x pos do mouse:0.0 y pos do mouse:4.0 z pos do mouse:36.0
//x dir do mouse:0.05769913 y dir do mouse:0.15393291 z dir do mouse:-0.9863951
        Vector3f localTiro = new Vector3f(0.0f, 4.0f, 36.0f);
        Vector3f direcaoTiro = new Vector3f(0.05769913f, 0.15393291f, -0.9863951f);
        /**
         * Position the cannon ball
         */
//    ball_geo.setLocalTranslation(cam.getLocation());
        ball_geo.setLocalTranslation(localTiro);
        /**
         * Make the ball physcial with a mass > 0.0f
         */
        ball_phy = new RigidBodyControl(15.0f);
        /**
         * Add physical ball to physics space.
         */
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);

        bulletAppState.getPhysicsSpace().add(ball_phy);
        ball_phy.setFriction(0.8f);
        ball_phy.setDamping(0.0f, 0.0f);
        ball_phy.setSleepingThresholds(1.0f, 1.00f);

        /**
         * Accelerate the physcial ball to shoot it.
         */
//    ball_phy.setLinearVelocity(cam.getDirection().mult(50));
        ball_phy.setLinearVelocity(direcaoTiro.mult(30));
    }

    /**
     * A plus sign used as crosshairs to help the player with aiming.
     */
    protected void initCrossHairs() {
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+");        // fake crosshairs :)
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
}