package CGA.User.DataStructures.Geometry;

import CGA.User.DataStructures.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Fabian on 19.09.2017.
 */

/**
 * Renders Mesh objects
 */
public class Renderable extends Transformable implements IRenderable
{
    /**
     * List of meshes attached to this renderable object
     */
    public ArrayList<Mesh> meshes;

    /**
     * creates rand Positon
     */
    public void randPositon(){
        Random rand = new Random();
        int f = rand.nextInt();
        // hier wird die randomzahl f zwischen 0 und 21 verwandelt
        int x = f % 21;

        Random rand2 = new Random();
        int f2 = rand2.nextInt();
        // hier wird die randomzahl f2 zwischen 0 und 21 verwandelt
        int z = f2 % 21;

        // es wird verhindert das der goldenBall in der selbe stelle wie ein Baum auftaucht
        if( x == 8 && z == 0||x == -2 && z == 0||x == 8 && z == 2){
            x+=10; z+=10;
        }

        this.setPositonChange(new Vector3f((float)x,0.01f,(float)z));

    }






    /**
     * creates an empty renderable object with an empty mesh list
     */
    public Renderable()
    {
        super();
        meshes = new ArrayList<>();
    }

    public Renderable(ArrayList<Mesh> meshes)
    {
        super();
        this.meshes = new ArrayList<>();
        this.meshes.addAll(meshes);
    }

    /**
     * Renders all meshes attached to this Renderable, applying the transformation matrix to
     * each of them
     */
    public void render()
    {
        for(Mesh m : meshes)
        {
            m.render();
        }
    }

    @Override
    public void render(ShaderProgram shaderProgram){
        shaderProgram.setUniform("model_matrix", getWorldModelMatrix(), false);
        for(Mesh m : meshes)
        {
            m.render(shaderProgram);
        }
    }
}