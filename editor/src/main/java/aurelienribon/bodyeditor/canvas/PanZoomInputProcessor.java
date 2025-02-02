package aurelienribon.bodyeditor.canvas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */

 
public class PanZoomInputProcessor extends InputAdapter {
    private final Canvas canvas;
    private final Vector2 lastTouch = new Vector2();
    private final int[] zoomLevels = { 1, 5, 10, 16, 25, 33, 50, 66, 100, 150, 200, 300, 400, 600, 800, 1200 };
    private int zoomLevel =  400;

    private int zoomMin = 20;
    private int zoomMax = 1330;

    public PanZoomInputProcessor(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (button != Buttons.RIGHT)
            return false;

        Vector2 p = canvas.screenToWorld(x, y);
        lastTouch.set(p);
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (!Gdx.input.isButtonPressed(Buttons.RIGHT))
            return false;

        Vector2 p = canvas.screenToWorld(x, y);
        Vector2 delta = new Vector2(p).sub(lastTouch);
        canvas.worldCamera.translate(-delta.x, -delta.y, 0);
        canvas.worldCamera.update();
        lastTouch.set(canvas.screenToWorld(x, y));
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        float amount = amountX == 0 ? amountY : amountX;

       zoomLevel = (int) canvas.worldCamera.zoom;
        if (zoomLevel <= zoomMin && amount < 0) {
            zoomLevel = zoomMin;
        } else if (zoomLevel == zoomMax && amount > 0) {
            zoomLevel = zoomMax;
        } else {
            zoomLevel = zoomLevel + (int) (amount*10);
            if (zoomLevel < zoomMin ){
                zoomLevel = zoomMin;
            }else if(zoomLevel > zoomMax){
                zoomLevel = zoomMax;
            }
            
        }
       
        canvas.worldCamera.zoom = zoomLevel;
        canvas.worldCamera.update();
        return false;
    }
}
