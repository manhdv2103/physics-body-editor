package aurelienribon.bodyeditor.canvas;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;

import java.text.DecimalFormat;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import aurelienribon.bodyeditor.Settings;
import aurelienribon.bodyeditor.models.DynamicObjectModel;
import aurelienribon.bodyeditor.models.PolygonModel;
import aurelienribon.bodyeditor.models.RigidBodyModel;
import aurelienribon.bodyeditor.models.ShapeModel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class CanvasDrawer {
    private static final Color SHAPE_COLOR = new Color(0.0f, 0.0f, 0.8f, 1);
    private static final Color SHAPE_LASTLINE_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);
    private static final Color POLYGON_COLOR = new Color(0.0f, 0.7f, 0.0f, 1);
    private static final Color ORIGIN_COLOR = new Color(0.7f, 0.0f, 0.0f, 1);
    private static final Color MOUSESELECTION_FILL_COLOR = new Color(0.2f, 0.2f, 0.8f, 0.2f);
    private static final Color MOUSESELECTION_STROKE_COLOR = new Color(0.2f, 0.2f, 0.8f, 0.6f);
    private static final Color GRID_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);
    private static final Color AXIS_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);
    private static final Color AXIS_COLOR_Y = new Color(0.0f, 255.0f, 0.0f, 1);
    private static final Color AXIS_COLOR_X = new Color(255.0f, 0.0f, 0.0f, 1);

    private final ShapeRenderer drawer = new ShapeRenderer();
    private final SpriteBatch batch;
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();
    private final DecimalFormat decimalFormat = new DecimalFormat("#");
    private final OrthographicCamera camera;
    private final Sprite v00Sprite;
    private final Sprite v10Sprite;
    private final Sprite v01Sprite;

    public CanvasDrawer(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;

        v00Sprite = new Sprite(Assets.inst().get("data/v00.png", Texture.class));
        v10Sprite = new Sprite(Assets.inst().get("data/v10.png", Texture.class));
        v01Sprite = new Sprite(Assets.inst().get("data/v01.png", Texture.class));
        v00Sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        v10Sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        v01Sprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        v00Sprite.setColor(AXIS_COLOR);
        v10Sprite.setColor(AXIS_COLOR);
        v01Sprite.setColor(AXIS_COLOR);

        font.setUseIntegerPositions(false);
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public void drawModel(RigidBodyModel model, List<Vector2> selectedPoints, Vector2 nextPoint, Vector2 nearestPoint) {
        if (model == null)
            return;

        drawer.setProjectionMatrix(camera.combined);
        drawer.setTransformMatrix(new Matrix4());

        if (Settings.isPolygonDrawn) {
            drawPolygons(model.getPolygons());
        }

        if (Settings.isShapeDrawn) {
            drawShapes(model.getShapes(), nextPoint);
            drawPoints(model.getShapes(), selectedPoints, nearestPoint, nextPoint);

            drawOrigin(model.getOrigin(), nearestPoint);
        }
    }

    public void drawModel(RigidBodyModel model, DynamicObjectModel.BodyAttributes attrs) {
        if (model == null)
            return;

        Matrix4 transform = new Matrix4();
        transform.translate(attrs.x, attrs.y, 0);
        transform.scale(attrs.scale, attrs.scale, 0);

        drawer.setProjectionMatrix(camera.combined);
        drawer.setTransformMatrix(transform);

        if (Settings.isPolygonDrawn) {
            drawPolygons(model.getPolygons());
        }

        if (Settings.isShapeDrawn) {
            drawShapes(model.getShapes(), null);
        }
    }

    public void drawBoundingBox(Sprite sp) {
        if (sp == null)
            return;
        drawer.setProjectionMatrix(camera.combined);
        drawer.setTransformMatrix(new Matrix4());
        drawBoundingBox(sp.getWidth(), sp.getHeight());
    }

    public void drawAxis(Vector2 o) {
        drawer.setProjectionMatrix(camera.combined);
        drawer.setTransformMatrix(new Matrix4());
        if (Settings.isAxisShown)
            drawAxisImpl(o);
    }

    public void drawGrid() {
        drawer.setProjectionMatrix(camera.combined);
        drawer.setTransformMatrix(new Matrix4());
        if (Settings.isGridShown)
            drawGrid(Settings.gridGap);
    }

    public void drawMouseSelection(Vector2 p1, Vector2 p2) {
        if (p1 == null || p2 == null)
            return;
        drawer.setProjectionMatrix(camera.combined);
        drawer.setTransformMatrix(new Matrix4());
        drawMouseSelection(p1.x, p1.y, p2.x, p2.y);
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private void drawBoundingBox(float w, float h) {
        Gdx.gl.glLineWidth(1);
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        drawer.begin(ShapeRenderer.ShapeType.Line);
        drawer.setColor(AXIS_COLOR);
        drawer.rect(0, 0, w, h);
        drawer.end();
    }

    private void drawAxisImpl(Vector2 o) {

        Gdx.gl.glLineWidth(3);
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        float len = 0.03f * camera.zoom;
        // float len = 0.03f * camera.zoom;
        drawer.begin(ShapeRenderer.ShapeType.Line);
        drawer.setColor(AXIS_COLOR_X);
        drawer.line(o.x, o.y, o.x + 100, o.y);
        drawer.line(o.x + 100, o.y, o.x + 100 - len, o.y - len);
        drawer.line(o.x + 100, o.y, o.x + 100 - len, o.y + len);
        drawer.setColor(AXIS_COLOR_Y);
        drawer.line(o.x, o.y, o.x, o.y + 100);
        drawer.line(o.x, o.y + 100, o.x - len, o.y + 100 - len);
        drawer.line(o.x, o.y + 100, o.x + len, o.y + 100 - len);
        drawer.end();

        float size = 0.1f * camera.zoom;
        v00Sprite.setSize(size, size);
        v10Sprite.setSize(size, size);
        v01Sprite.setSize(size, size);
        v00Sprite.setPosition(o.x - size, o.y - size);
        v10Sprite.setPosition(o.x + 100, o.y - size);
        v01Sprite.setPosition(o.x - size, o.y + 100 - size / 2);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        v00Sprite.draw(batch);
        v10Sprite.draw(batch);
        v01Sprite.draw(batch);
        batch.end();
    }

    private void drawGrid(float gap) {
        Gdx.gl.glLineWidth(1);
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (gap <= 0)
            gap = 0.001f;
        float x = camera.position.x;
        float y = camera.position.y;
        float w = camera.viewportWidth;
        float h = camera.viewportHeight;
        float z = camera.zoom;

        drawer.begin(ShapeRenderer.ShapeType.Line);
        drawer.setColor(GRID_COLOR);
        for (float d = 0; d < x + w / 2 * z; d += gap)
            drawer.line(d, y - h / 2 * z, d, y + h / 2 * z);
        for (float d = -gap; d > x - w / 2 * z; d -= gap)
            drawer.line(d, y - h / 2 * z, d, y + h / 2 * z);
        for (float d = 0; d < y + h / 2 * z; d += gap)
            drawer.line(x - w / 2 * z, d, x + w / 2 * z, d);
        for (float d = -gap; d > y - h / 2 * z; d -= gap)
            drawer.line(x - w / 2 * z, d, x + w / 2 * z, d);
        drawer.end();
    }

    private void drawShapes(List<ShapeModel> shapes, Vector2 nextPoint) {
        Gdx.gl.glLineWidth(2);
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for (ShapeModel shape : shapes) {
            List<Vector2> vs = shape.getVertices();
            if (vs.isEmpty())
                continue;

            switch (shape.getType()) {
            case POLYGON:
                drawer.begin(ShapeRenderer.ShapeType.Line);
                drawer.setColor(SHAPE_COLOR);

                for (int i = 1; i < vs.size(); i++)
                    drawer.line(vs.get(i).x, vs.get(i).y, vs.get(i - 1).x, vs.get(i - 1).y);

                if (shape.isClosed()) {
                    drawer.setColor(SHAPE_COLOR);
                    drawer.line(vs.get(0).x, vs.get(0).y, vs.get(vs.size() - 1).x, vs.get(vs.size() - 1).y);
                } else {

                    if (nextPoint != null) {
                        drawer.setColor(SHAPE_LASTLINE_COLOR);
                        drawer.line(vs.get(vs.size() - 1).x, vs.get(vs.size() - 1).y, nextPoint.x, nextPoint.y);
                    }

                }

                drawer.end();
                break;

            case CIRCLE:
                if (shape.isClosed()) {
                    Vector2 center = shape.getVertices().get(0);
                    float radius = shape.getVertices().get(1).cpy().sub(center).len();
                    if (radius > 0.0001f) {
                        drawer.begin(ShapeRenderer.ShapeType.Line);
                        drawer.setColor(SHAPE_COLOR);
                        drawer.circle(center.x, center.y, radius, 20);
                        drawer.end();
                    }
                } else {
                    Vector2 center = shape.getVertices().get(0);
                    float radius = nextPoint.cpy().sub(center).len();

                    if (radius > 0.0001f) {
                        drawer.begin(ShapeRenderer.ShapeType.Line);
                        drawer.setColor(SHAPE_LASTLINE_COLOR);
                        drawer.circle(center.x, center.y, radius, 20);
                        drawer.end();
                    }
                }
                break;
            }
        }
    }

    private void drawPoints(List<ShapeModel> shapes, List<Vector2> selectedPoints, Vector2 nearestPoint,
            Vector2 nextPoint) {
        Gdx.gl.glLineWidth(2);
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        float w = 0.025f * camera.zoom;

        for (ShapeModel shape : shapes) {
            for (Vector2 p : shape.getVertices()) {
                if (p == nearestPoint || (selectedPoints != null && selectedPoints.contains(p))) {
                    drawer.begin(ShapeRenderer.ShapeType.Filled);


                } else {
                    drawer.begin(ShapeRenderer.ShapeType.Line);
                }
                drawer.setColor(SHAPE_COLOR);
                drawer.rect(p.cpy().sub(w / 2, w / 2).x, p.cpy().sub(w / 2, w / 2).y, w, w);
                drawer.end();
            }
        }

        if (nextPoint != null) {
            drawer.begin(ShapeRenderer.ShapeType.Line);
            drawer.setColor(SHAPE_LASTLINE_COLOR);
            drawer.rect(nextPoint.cpy().sub(w / 2, w / 2).x, nextPoint.cpy().sub(w / 2, w / 2).y, w, w);
            drawer.end();
        }

        if (nearestPoint != null) {
            String coord = String.format("(%s, %s)", decimalFormat.format(nearestPoint.x), decimalFormat.format(nearestPoint.y));

            font.getData().setScale(0.003f * camera.zoom);
            layout.setText(font, coord);
            float width = layout.width * 1.1f;
            float height = layout.height * 1.6f;

            drawer.begin(ShapeRenderer.ShapeType.Filled);
            drawer.setColor(Color.DARK_GRAY.r, Color.DARK_GRAY.g, Color.DARK_GRAY.b, 0.8f);
            drawer.rect(nearestPoint.cpy().sub(w / 2, w / 2).x + 0.05f * camera.zoom - layout.width * 0.05f, nearestPoint.cpy().sub(w / 2, w / 2).y - height + layout.height * 0.2f, width, height);
            drawer.end();

            batch.begin();
            font.setColor(Color.WHITE);
            font.draw(batch, coord, nearestPoint.cpy().sub(w / 2, w / 2).x + 0.05f * camera.zoom, nearestPoint.cpy().sub(w / 2, w / 2).y);
            batch.end();
        }
    }

    private void drawPolygons(List<PolygonModel> polygons) {
        Gdx.gl.glLineWidth(2);
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        drawer.begin(ShapeRenderer.ShapeType.Line);
        drawer.setColor(POLYGON_COLOR);

        for (PolygonModel polygon : polygons) {
            List<Vector2> vs = polygon.vertices;
            for (int i = 1, n = vs.size(); i < n; i++)
                drawer.line(vs.get(i).x, vs.get(i).y, vs.get(i - 1).x, vs.get(i - 1).y);
            if (vs.size() > 1)
                drawer.line(vs.get(0).x, vs.get(0).y, vs.get(vs.size() - 1).x, vs.get(vs.size() - 1).y);
        }

        drawer.end();
    }

    private void drawOrigin(Vector2 o, Vector2 nearestPoint) {
        Gdx.gl.glLineWidth(2);
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        float len = 0.03f * camera.zoom;
        float radius = 0.02f * camera.zoom;

        drawer.begin(ShapeRenderer.ShapeType.Line);
        drawer.setColor(ORIGIN_COLOR);
        drawer.line(o.x - len, o.y, o.x + len, o.y);
        drawer.line(o.x, o.y - len, o.x, o.y + len);
        drawer.end();

        if (nearestPoint != o) {
            drawer.begin(ShapeRenderer.ShapeType.Line);
            drawer.setColor(ORIGIN_COLOR);
            drawer.circle(o.x, o.y, radius, 20);
            drawer.end();
        } else {
            drawer.begin(ShapeRenderer.ShapeType.Filled);
            drawer.setColor(ORIGIN_COLOR);
            drawer.circle(o.x, o.y, radius, 20);
            drawer.end();
        }
    }

    private void drawMouseSelection(float x1, float y1, float x2, float y2) {
        Gdx.gl.glLineWidth(3);
        Gdx.gl.glEnable(GL_BLEND);
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Rectangle rect = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));

        drawer.begin(ShapeRenderer.ShapeType.Filled);
        drawer.setColor(MOUSESELECTION_FILL_COLOR);
        drawer.rect(rect.x, rect.y, rect.width, rect.height);
        drawer.end();

        drawer.begin(ShapeRenderer.ShapeType.Line);
        drawer.setColor(MOUSESELECTION_STROKE_COLOR);
        drawer.rect(rect.x, rect.y, rect.width, rect.height);
        drawer.end();
    }
}
