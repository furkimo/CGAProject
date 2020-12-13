package CGA.User.Game;

import CGA.Framework.GameWindow;
import CGA.Framework.ModelLoader;
import CGA.Framework.OBJLoader;
import CGA.User.DataStructures.Camera.TronCam;
import CGA.User.DataStructures.Geometry.*;
import CGA.User.DataStructures.Light.PointLight;
import CGA.User.DataStructures.Light.SpotLight;
import CGA.User.DataStructures.ShaderProgram;
import CGA.User.DataStructures.Texture2D;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import java.util.Random;
import java.awt.*;
import java.awt.image.BufferedImage;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


public class Scene {
    private ShaderProgram simpleShader;
    private GameWindow window;
    private VertexAttribute[] attributes,attributesCoins;
    private Renderable ground;
    private Renderable baum;
    private Renderable baum2;

    private Renderable userBall;
    private Renderable himmel;
    private Renderable goldenBall,badBall;
    private TronCam tronCam;
    private double oldmousex;
    private double oldmousey;
    private Material groundmat;
    private PointLight ball2Light;
    private PointLight sonne;
    private SpotLight ball2Lightspot;
    private int countBalls = 0;
    private int xBadBall = 0;
    private int zBadBall = 0;

    public Scene(GameWindow window) {
        this.window = window;

    }

    //scene setup
    public boolean init() {
        try {
            glClearColor(117.0f, 142.0f, 225.0f, 1.0f);
            //Load staticShader
            simpleShader = new ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl");

            //TODO: Place your code here. Specify the geometry data and create the needed mesh object.

            //Objekt laden und Mesh erzeugen
            OBJLoader.OBJResult resgr = OBJLoader.loadOBJ("assets/models/ground.obj", false, false);

            OBJLoader.OBJMesh objMeshgr;

            goldenBall = ModelLoader.loadModel("assets/models/Beach_Ball/13517_Beach_Ball_v2_L3.obj", (float) Math.toRadians(-90), (float) Math.toRadians(90.0f), 0.0f);
            goldenBall.scaleLocal(new Vector3f(0.04f));
            goldenBall.randPositon();

            badBall = ModelLoader.loadModel("assets/models/BadBall/futbol.obj", (float) Math.toRadians(-90), (float) Math.toRadians(90.0f), 0.0f);
            badBall.scaleLocal(new Vector3f(0.04f));
            badBall.translateLocal(new Vector3f(400.0f,0.0f,-400.0f));


            baum = ModelLoader.loadModel("assets/models/Tree/Tree.obj", (float) Math.toRadians(0), (float) Math.toRadians(90.0f), 0.0f);
            baum.scaleLocal(new Vector3f(2.0f));
            baum.translateLocal(new Vector3f(8.0f,0.0f,0.0f));
            baum2 = ModelLoader.loadModel("assets/models/Tree/Tree.obj", (float) Math.toRadians(0), (float) Math.toRadians(90.0f), 0.0f);
            baum2.scaleLocal(new Vector3f(2.0f));
            baum2.translateLocal(new Vector3f(-2.0f,0.0f,0.0f));
            userBall = ModelLoader.loadModel("assets/models/moon/Moon.obj", (float) Math.toRadians(0), (float) Math.toRadians(90.0f), 0.0f);
            userBall.scaleLocal(new Vector3f(1.0f));
            userBall.translateLocal(new Vector3f(3.0f,1.0f,11.0f));

            himmel = ModelLoader.loadModel("assets/models/ball/soccer.obj", (float) Math.toRadians(0), (float) Math.toRadians(20.0f), 0.0f);
            himmel.scaleLocal(new Vector3f(4.0f));
            himmel.translateLocal(new Vector3f(7.0f,1.0f,1.0f));


            //Erstes Mesh aus der OBJ Datei
            objMeshgr = resgr.objects.get(0).meshes.get(0);



            attributes = new VertexAttribute[3]; //Vertexattribute für Kreis und Ground
            attributes[0] = new VertexAttribute(3, GL_FLOAT, 32, 0);
            attributes[1] = new VertexAttribute(2, GL_FLOAT, 32, 12);
            attributes[2] = new VertexAttribute(3, GL_FLOAT, 32, 20);


            Texture2D grounddiff = new Texture2D("assets/textures/ground_diff.png", true);
            grounddiff.setTexParams(GL_CLAMP, GL_REPEAT, GL_LINEAR, GL_LINEAR);
            Texture2D groundemit = new Texture2D("assets/textures/ground_emit.png", true);
            groundemit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR);
            Texture2D groundspec = new Texture2D("assets/textures/ground_spec.png", true);
            groundspec.setTexParams(GL_CLAMP, GL_REPEAT, GL_LINEAR, GL_LINEAR);

            groundmat = new Material(grounddiff, groundemit, groundspec, 60.0f, new Vector2f(64.0f, 64.0f));

            ground = new Renderable();
            ground.meshes.add(new Mesh(objMeshgr.getVertexData(), objMeshgr.getIndexData(), attributes, groundmat));



            tronCam = new TronCam();
            tronCam.translateLocal(new Vector3f(0.0f, 2.0f, 4.0f));
            tronCam.setParent(userBall);


            ball2Lightspot = new SpotLight(0.0f,3.0f,0.0f,1.0f,1.0f,1.0f, 0.5f, 0.05f, 0.01f, 10.0f, 30.0f);
            ball2Lightspot.translateLocal(new Vector3f(0.0f,1.0f,-2.0f));
            ball2Lightspot.rotateLocal((float)Math.toRadians(-10.0f),0,0);



            ball2Light = new PointLight(0.0f,0.0f,0.0f, 1.0f,0.0f,0.0f, 1.0f, 0.5f, 0.1f);
            ball2Light.translateLocal(new Vector3f(0.0f,0.3f,0.0f));
            //ball2Light.setParent(ball2);

            sonne = new PointLight(0.0f,3.0f,0.0f,      1.0f,1.0f,1.0f,      1.0f, 0.5f, 0.1f);
            sonne.translateLocal(new Vector3f(0.0f,1.3f,0.0f));

            oldmousex = window.getMousePos().xpos;
            oldmousey = window.getMousePos().ypos;


            glEnable(GL_CULL_FACE);
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);

            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);


            return true;
        } catch (Exception ex) {
            System.err.println("Scene initialization failed:\n" + ex.getMessage() + "\n");
            return false;
        }

    }



    public void render(float dt, float t) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        simpleShader.use();

        simpleShader.setUniform("bodenfarbe", new Vector3f(0.0f,1.0f,0.0f));

        sonne.bind(simpleShader, "pointlight");

        ball2Lightspot.bind(simpleShader, "spotlight", tronCam.getViewMatrix());

        tronCam.bind(simpleShader);

        ground.render(simpleShader);

        goldenBall.render(simpleShader);

        badBall.render(simpleShader);

        baum.render(simpleShader);

        baum2.render(simpleShader);

        userBall.render(simpleShader);

        himmel.render(simpleShader);

    }

    boolean ntoRotated = true;
    int i = 1;
    public void update(float dt, float t) {

        //badBall den user folgen

        if((int)badBall.getPosition().x<(int)xBadBall){// die methode badBall.getPosition().x gibt die X Koordinate von der badBall zurück

            badBall.translateLocal(new Vector3f(0.5f, 0.0f, 0.0f));
        }else if((int)badBall.getPosition().x>(int)xBadBall){
            badBall.translateLocal(new Vector3f(-0.5f, 0.0f, 0.0f));
        }else{
            xBadBall = (int) userBall.getPosition().x;
            zBadBall = (int) userBall.getPosition().z;
        }

        if((int)badBall.getPosition().z<(int)zBadBall){
            badBall.translateLocal(new Vector3f(0.0f, 0.0f, 0.5f));
        }else if((int)badBall.getPosition().z>(int)zBadBall){
            badBall.translateLocal(new Vector3f(0.0f, 0.0f, -0.5f));
        }else{
            xBadBall = (int) userBall.getPosition().x;
            zBadBall = (int) userBall.getPosition().z;
        }

        //badBall den user folgen ende

        //win
        if (countBalls == 3) {
            goldenBall.scaleLocal(new Vector3f(0.0001f));// goldenBall verkleinert

            while(ntoRotated) {
                //Cam drehen
                tronCam.setParent(ground);
                tronCam.rotateLocal(-1.6f, 0f, 0.0f);
                tronCam.translateLocal(new Vector3f(0f, 0.0f, 20.01f));
                ntoRotated = false;
            }


            if(i <=999) {
                //Cam move nach oben langsam
                tronCam.translateLocal(new Vector3f(0.0f, 0.0f, 0.01f));
                i++;
            }
            if(i == 999) {
                window.quit();
            }

        }

        //Lose
        if(userBall.hasCollision(badBall) & countBalls != 3) {// die Methode userBall.hasCollision(badBall) gibt mir eine True oder False ob 2 Objekte sich getroffen haben.
            window.quit();
        }

        //################################################################ fallen vom rand ################################################################

        if(userBall.getPosition().x >= 24 ){
            tronCam.setParent(ground);
            while(ntoRotated) {
                // Cam wird gedreht
                tronCam.rotateLocal(0.0f, 1.6f, 0.0f);
                ntoRotated = false;
            }
            // Cam Position wird geändert
            tronCam.translateLocal(new Vector3f(0.0f, 0.05f, 0.3f));

            //wenn der Cam X Koordinate >= 34 der ball fellt runter
            if(tronCam.getPosition().x>=34){
                userBall.translateLocal(new Vector3f(0.0f, -0.07f, 0.0f));
            }

            // wenn die Y Koordinate von der UserBall <= -20 dann ist das spiel zu ende
            if(userBall.getPosition().y <= -20){
                window.quit();
            }
        }

        if(userBall.getPosition().x <=-24){
            tronCam.setParent(ground);
            while(ntoRotated) {
                tronCam.rotateLocal(0.0f, -1.5f, 0.0f);
                ntoRotated = false;
            }

            tronCam.translateLocal(new Vector3f(0.0f, 0.05f, 0.3f));
            if(tronCam.getPosition().x<=-34){
                userBall.translateLocal(new Vector3f(0.0f, -0.07f, 0.0f));
            }
            if(userBall.getPosition().y<=-20){
                window.quit();
            }
        }

        if(userBall.getPosition().z>=24){
            tronCam.setParent(ground);
            tronCam.translateLocal(new Vector3f(0.00f, 0.05f, 0.3f));
            if(tronCam.getPosition().z>=34){
                userBall.translateLocal(new Vector3f(0.0f, -0.07f, 0.0f));
            }
            if(userBall.getPosition().y<=-20){
                window.quit();
            }
        }

        if(userBall.getPosition().z <=-24){
            tronCam.setParent(ground);
            while(ntoRotated) {
                tronCam.rotateLocal(0.0f, 3.43f, 0.0f);
                ntoRotated = false;
            }
            tronCam.translateLocal(new Vector3f(0.00f, 0.05f, 0.3f));
            if(tronCam.getPosition().z<=-34){
                userBall.translateLocal(new Vector3f(0.0f, -0.07f, 0.0f));
            }
            if(userBall.getPosition().y <= -20){
                window.quit();
            }
        }
        //################################################################ fallen vom rand ende ################################################################

        //wenn der user D drückt was soll passieren
        if (window.getKeyState(GLFW_KEY_D)) {
            userBall.rotateLocal(0.0f, -0.01f, 0.0f);



            if(userBall.hasCollision(baum)|| userBall.hasCollision(baum2)) {
                userBall.translateLocal(new Vector3f(0.0f, 0.01f, 0.0f));
            }

        }

        //wenn der user W drückt was soll passieren
        if (window.getKeyState(GLFW_KEY_W)) {
            userBall.translateLocal(new Vector3f(0.0f, 0.0f, -0.1f));
            if(userBall.hasCollision(baum)|| userBall.hasCollision(baum2)) {
                userBall.translateLocal(new Vector3f(0.0f, 0.0f, 0.1f));
            }
            if(userBall.hasCollision(goldenBall)) {
                goldenBall.randPositon();
                countBalls++;

                //
                Random rand = new Random();
                int f = rand.nextInt();
                xBadBall = f % 21;

                Random rand2 = new Random();
                int f2 = rand2.nextInt();
                zBadBall  = f2 % 21;

            }


        }

        //wenn der user A drückt was soll passieren
        if (window.getKeyState(GLFW_KEY_A)) {
            userBall.rotateLocal(0.0f, 0.01f, 0.0f);
            if(userBall.hasCollision(baum)|| userBall.hasCollision(baum2)) {
                userBall.translateLocal(new Vector3f(0.0f, -0.01f, 0.0f));
            }
            if(userBall.hasCollision(goldenBall)) {
                goldenBall.randPositon();
                countBalls++;
            }

        }

        //wenn der user S drückt was soll passieren
        if (window.getKeyState(GLFW_KEY_S)) {
            userBall.translateLocal(new Vector3f(0.0f, 0.0f, 0.1f));
            if(userBall.hasCollision(baum)|| userBall.hasCollision(baum2)) {
                userBall.translateLocal(new Vector3f(0.0f, 0.0f, -0.1f));
            }
            if(userBall.hasCollision(goldenBall)) {
                goldenBall.randPositon();
                countBalls++;
            }

        }


        if (oldmousex > window.getMousePos().xpos) {
            tronCam.rotateAroundPoint(0f, -(float) (oldmousex - window.getMousePos().xpos) / 1000, 0f, userBall.getPosition().add(0.0f,0.0f,4.0f).negate());
            //tronCam.translateLocal(new Vector3f(-(float) (oldmousex - window.getMousePos().xpos) / 200,0.0f,0.0f));
            oldmousex = window.getMousePos().xpos;
        }
        if (oldmousex < window.getMousePos().xpos) {
            tronCam.rotateAroundPoint(0f, (float) (window.getMousePos().xpos - oldmousex) / 1000, 0f, userBall.getPosition().add(0.0f,0.0f,4.0f).negate());
            //tronCam.translateLocal(new Vector3f(-(float) (oldmousex - window.getMousePos().xpos) / 200,0.0f,0.0f));
            oldmousex = window.getMousePos().xpos;
        }





    }

    public void onKey(int key, int scancode, int action, int mode) {

    }

    public void onMouseMove(double xpos, double ypos) {

    }


    public void cleanup() {
    }








}