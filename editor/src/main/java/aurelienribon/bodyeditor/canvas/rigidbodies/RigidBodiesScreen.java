package aurelienribon.bodyeditor.canvas.rigidbodies;

import static aurelienribon.bodyeditor.canvas.Canvas.worldCamera;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import aurelienribon.bodyeditor.Ctx;
import aurelienribon.bodyeditor.RigidBodiesManager;
import aurelienribon.bodyeditor.Settings;
import aurelienribon.bodyeditor.canvas.Assets;
import aurelienribon.bodyeditor.canvas.Canvas;
import aurelienribon.bodyeditor.canvas.Label;
import aurelienribon.bodyeditor.canvas.Label.Anchor;
import aurelienribon.bodyeditor.canvas.rigidbodies.input.CreationInputProcessor;
import aurelienribon.bodyeditor.canvas.rigidbodies.input.EditionInputProcessor;
import aurelienribon.bodyeditor.canvas.rigidbodies.input.TestInputProcessor;
import aurelienribon.bodyeditor.maths.Tracer;
import aurelienribon.bodyeditor.models.CircleModel;
import aurelienribon.bodyeditor.models.PolygonModel;
import aurelienribon.bodyeditor.models.RigidBodyModel;
import aurelienribon.bodyeditor.models.ShapeModel;
import aurelienribon.bodyeditor.ui.AutoTraceParamsDialog;
import aurelienribon.bodyeditor.utils.ShapeUtils;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.utils.gdx.PolygonUtils;
import aurelienribon.utils.notifications.ChangeListener;
import aurelienribon.utils.notifications.ObservableList;

public class RigidBodiesScreen {
    private Vector2 v0 = new Vector2(0, 0);
    private final Canvas canvas;
    private final RigidBodiesScreenDrawer drawer;
    private final Box2DDebugRenderer debugRdr = new Box2DDebugRenderer();
    private final TweenManager tweenManager = new TweenManager();
    private float timeAcc = 0;

    private final List<Sprite> ballsSprites = new ArrayList<Sprite>();
    private final List<Body> ballsBodies = new ArrayList<Body>();
    private final World world = new World(new Vector2(0, 0), true);
    public static Sprite bodySprite;

    private final InputProcessor creationInputProcessor;
    private final InputProcessor editionInputProcessor;
    private final InputProcessor testInputProcessor;

    private final List<Label> labels = new ArrayList<Label>();
    private final Label lblModeCreation;
    private final Label lblModeEdition;
    private final Label lblModeTest;
    private final Label lblSetImage;
    private final Label lblClearImage;
    private final Label lblAutoTrace;
    private final Label lblClearVertices;
    private final Label lblInsertVertices;
    private final Label lblRemoveVertices;

    private enum Mode {
        CREATION, EDITION, TEST
    }

    private Mode mode = null;

    public final ObservableList<Vector2> selectedPoints = new ObservableList<Vector2>();
    public Vector2 nextPoint;
    public Vector2 nearestPoint;
    public Vector2 mouseSelectionP1;
    public Vector2 mouseSelectionP2;
    public Vector2 ballThrowP1;
    public Vector2 ballThrowP2;

    public RigidBodiesScreen(Canvas canvas) {
        this.canvas = canvas;
        this.drawer = new RigidBodiesScreenDrawer(worldCamera);

        creationInputProcessor = new CreationInputProcessor(canvas, this);
        editionInputProcessor = new EditionInputProcessor(canvas, this);
        testInputProcessor = new TestInputProcessor(canvas, this);
        canvas.input.addProcessor(modeInputProcessor);
        canvas.input.addProcessor(buttonsInputProcessor);
        canvas.input.addProcessor(creationInputProcessor);

        int lblH = 25;
        java.awt.Color menuColor = UIManager.getColor("menu");
        Color lblC = new Color(menuColor.getRed() / 255f, menuColor.getGreen() / 255f, menuColor.getBlue() / 255f, 200 / 255f);

        lblModeCreation = new Label(10 + lblH, 80, lblH, "CREATE", canvas.font, lblC, Anchor.TOP_LEFT);
        lblModeEdition = new Label(10 + lblH * 2, 80, lblH, "EDIT", canvas.font, lblC, Anchor.TOP_LEFT);
        lblModeTest = new Label(10 + lblH * 3, 80, lblH, "TEST", canvas.font, lblC, Anchor.TOP_LEFT);

        lblSetImage = new Label(10 + lblH, 120, lblH, "Load Image", canvas.font, lblC, Anchor.TOP_RIGHT);
        lblClearImage = new Label(15 + lblH * 2, 120, lblH, "Delete Image", canvas.font, lblC, Anchor.TOP_RIGHT);
        lblAutoTrace = new Label(20 + lblH * 4, 120, lblH, "Auto-trace", canvas.font, lblC, Anchor.TOP_RIGHT);
        lblClearVertices = new Label(25 + lblH * 5, 120, lblH, "Clear all points", canvas.font, lblC, Anchor.TOP_RIGHT);
        lblInsertVertices = new Label(30 + lblH * 6, 120, lblH, "Insert points", canvas.font, lblC, Anchor.TOP_RIGHT);
        lblRemoveVertices = new Label(35 + lblH * 7, 120, lblH, "Remove points", canvas.font, lblC, Anchor.TOP_RIGHT);

        labels.add(lblModeCreation);
        labels.add(lblModeEdition);
        labels.add(lblModeTest);
        labels.add(lblSetImage);
        labels.add(lblClearImage);
        labels.add(lblAutoTrace);
        labels.add(lblClearVertices);
        labels.add(lblInsertVertices);
        labels.add(lblRemoveVertices);

        initializeModelChangeListener();
        initializeSelectedPointsEvents();
        initializeModeLabelsCallbacks();
        initializeOtherLabelsCallbacks();

        final Tween reloadTimer = Tween.call(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                updateButtons();
            }
        }).repeat(-1, 0.3f).start(tweenManager);

        canvas.addListener(new Canvas.Listener() {
            @Override
            public void modeChanged(Canvas.Mode mode) {
                if (mode == Canvas.Mode.BODIES)
                    reloadTimer.resume();
                else
                    reloadTimer.pause();
            }
        });
    }

    public static void setPivot() {

        if (bodySprite != null) {
            Ctx.bodies.getSelectedModel().getOrigin().x = bodySprite.getWidth() / 2;
            Ctx.bodies.getSelectedModel().getOrigin().y = bodySprite.getHeight() / 2;
        }
    }

    private final InputProcessor modeInputProcessor = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            // NOT WORKING

            if (Ctx.bodies.getSelectedModel() != null) {
                if (keycode == Input.Keys.CONTROL_LEFT)
                    setNextMode();
            }

            return false;
        }
    };

    private final InputProcessor buttonsInputProcessor = new InputAdapter() {
        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {
            if (button == Input.Buttons.LEFT) {
                for (Label label : labels)
                    if (label.touchDown(x, y))
                        return true;
            }
            return false;
        }

        // @Override //TODO possibly mouseMoved? replace elsewhere as well
        public boolean touchMoved(int x, int y) {
            for (Label label : labels)
                label.touchMoved(x, y);
            return false;
        }
    };

    // -------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------

    private void initializeModelChangeListener() {
        final ChangeListener selectedModelChangeListener = new ChangeListener() {
            @Override
            public void propertyChanged(Object source, String propertyName) {
                if (propertyName.equals(RigidBodyModel.PROP_IMAGEPATH)) {

                    createBodySprite(false);
                    lblClearImage.show();
                } else if (propertyName.equals(RigidBodyModel.PROP_PHYSICS)) {
                    clearWorld();
                    createBody();
                }
            }
        };

        Ctx.bodies.addChangeListener(new ChangeListener() {
            private RigidBodyModel oldModel;

            @Override
            public void propertyChanged(Object source, String propertyName) {
                if (propertyName.equals(RigidBodiesManager.PROP_SELECTION)) {
                    RigidBodyModel model = Ctx.bodies.getSelectedModel();

                    setMode(model != null ? mode == null ? Mode.CREATION : mode : null);
                    updateButtons();
                    resetWorld();

                    if (model != null)
                        model.addChangeListener(selectedModelChangeListener);
                    if (oldModel != null)
                        oldModel.removeChangeListener(selectedModelChangeListener);
                    oldModel = model;
                }
            }
        });
    }

    private void initializeSelectedPointsEvents() {
        selectedPoints.addListChangedListener(new ObservableList.ListChangeListener<Vector2>() {
            @Override
            public void changed(Object source, List<Vector2> added, List<Vector2> removed) {
                RigidBodyModel model = Ctx.bodies.getSelectedModel();
                if (model == null)
                    return;

                List<Vector2> toAdd = new ArrayList<Vector2>();

                for (Vector2 v : added) {
                    ShapeModel shape = ShapeUtils.getShape(model, v);
                    if (shape == null)
                        continue;

                    if (shape.getType() == ShapeModel.Type.CIRCLE) {
                        List<Vector2> vs = shape.getVertices();
                        if (selectedPoints.contains(vs.get(0)) && !selectedPoints.contains(vs.get(1))) {
                            toAdd.add(vs.get(1));
                        }
                    }
                }

                selectedPoints.addAll(toAdd);
            }
        });
    }

    private void initializeModeLabelsCallbacks() {
        Label.TouchCallback modeLblCallback = new Label.TouchCallback() {
            @Override
            public void touchDown(Label source) {
                setNextMode();
                lblModeCreation.tiltOff();
                lblModeEdition.tiltOff();
                lblModeTest.tiltOff();
            }

            @Override
            public void touchEnter(Label source) {
                lblModeCreation.tiltOn();
                lblModeEdition.tiltOn();
                lblModeTest.tiltOn();
            }

            @Override
            public void touchExit(Label source) {
                lblModeCreation.tiltOff();
                lblModeEdition.tiltOff();
                lblModeTest.tiltOff();
            }
        };

        lblModeCreation.setCallback(modeLblCallback);
        lblModeEdition.setCallback(modeLblCallback);
        lblModeTest.setCallback(modeLblCallback);
    }

    private void initializeOtherLabelsCallbacks() {
        lblSetImage.setCallback(new Label.TouchCallback() {
            @Override
            public void touchEnter(Label source) {
            }

            @Override
            public void touchExit(Label source) {
            }

            @Override
            public void touchDown(Label source) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JFileChooser chooser = new JFileChooser(Ctx.io.getProjectDir());
                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        chooser.setDialogTitle("Select the background image for the selected model");
                        chooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg"));
                        chooser.setMultiSelectionEnabled(false);
                        chooser.setPreferredSize(new Dimension(800, 600));

                        if (chooser.showOpenDialog(Ctx.window) == JFileChooser.APPROVE_OPTION) {
                            String path = Ctx.io.buildImagePath(chooser.getSelectedFile());
                            Ctx.bodies.getSelectedModel().setImagePath(path);

                            Ctx.bodies.getSelectedModel().getOrigin().x = bodySprite.getWidth() / 2;
                            Ctx.bodies.getSelectedModel().getOrigin().y = bodySprite.getHeight() / 2;

                        }
                    }
                });
            }
        });

        lblClearImage.setCallback(new Label.TouchCallback() {
            @Override
            public void touchEnter(Label source) {
            }

            @Override
            public void touchExit(Label source) {
            }

            @Override
            public void touchDown(Label source) {
                RigidBodyModel model = Ctx.bodies.getSelectedModel();
                if (model != null) {
                    model.setImagePath(null);
                    createBodySprite(false);
                    lblClearImage.hide();
                }
            }
        });

        lblAutoTrace.setCallback(new Label.TouchCallback() {
            @Override
            public void touchEnter(Label source) {
            }

            @Override
            public void touchExit(Label source) {
            }

            @Override
            public void touchDown(Label source) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        AutoTraceParamsDialog dialog = new AutoTraceParamsDialog(Ctx.window);
                        dialog.setLocationRelativeTo(Ctx.window);
                        if (dialog.prompt())
                            autoTrace();
                    }
                });
            }
        });

        lblClearVertices.setCallback(new Label.TouchCallback() {
            @Override
            public void touchEnter(Label source) {
            }

            @Override
            public void touchExit(Label source) {
            }

            @Override
            public void touchDown(Label source) {
                clearPoints();
            }
        });

        lblInsertVertices.setCallback(new Label.TouchCallback() {
            @Override
            public void touchEnter(Label source) {
            }

            @Override
            public void touchExit(Label source) {
            }

            @Override
            public void touchDown(Label source) {
                insertPointsBetweenSelected();
            }
        });

        lblRemoveVertices.setCallback(new Label.TouchCallback() {
            @Override
            public void touchEnter(Label source) {
            }

            @Override
            public void touchExit(Label source) {
            }

            @Override
            public void touchDown(Label source) {
                removeSelectedPoints();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Render
    // -------------------------------------------------------------------------

    public void render() {
        while (timeAcc < Gdx.graphics.getDeltaTime()) {
            timeAcc += 1f / 60;
            world.step(1f / 60, 10, 10);
        }

        timeAcc -= Gdx.graphics.getDeltaTime();
        tweenManager.update(Gdx.graphics.getDeltaTime());

        canvas.drawer.drawBoundingBox(bodySprite);

        if (Ctx.bodies.getSelectedModel() == null) {
            canvas.drawer.drawAxis(v0);
        } else {
            canvas.drawer.drawAxis(Ctx.bodies.getSelectedModel().getOrigin());
        }

        canvas.batch.setProjectionMatrix(canvas.worldCamera.combined);
        canvas.batch.begin();
        if (bodySprite != null && Settings.isImageDrawn)
            bodySprite.draw(canvas.batch);
        for (int i = 0; i < ballsSprites.size(); i++) {
            Sprite sp = ballsSprites.get(i);
            Vector2 pos = ballsBodies.get(i).getPosition();
            float angle = ballsBodies.get(i).getAngle() * MathUtils.radiansToDegrees;
            sp.setPosition(pos.x - sp.getWidth() / 2, pos.y - sp.getHeight() / 2);
            sp.setRotation(angle);
            sp.draw(canvas.batch);
        }
        canvas.batch.end();

        // nearestPoint = new Vector2(50, 50);
        canvas.drawer.drawModel(Ctx.bodies.getSelectedModel(), selectedPoints, nextPoint, nearestPoint);
        canvas.drawer.drawGrid();
        canvas.drawer.drawMouseSelection(mouseSelectionP1, mouseSelectionP2);
        drawer.drawBallThrowPath(ballThrowP1, ballThrowP2);

        if (Settings.isPhysicsDebugEnabled) {
            debugRdr.render(world, canvas.worldCamera.combined);
        }

        canvas.batch.setProjectionMatrix(canvas.screenCamera.combined);
        canvas.batch.begin();
        for (Label lbl : labels)
            lbl.draw(canvas.batch);
        canvas.batch.end();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public void buildBody() {
        clearWorld();
        createBody();
    }

    public void fireBall(Vector2 orig, Vector2 force) {
        createBall(orig, force);
    }

    public void insertPointsBetweenSelected() {
        if (!isInsertEnabled())
            return;

        List<Vector2> toAdd = new ArrayList<Vector2>();

        for (ShapeModel shape : Ctx.bodies.getSelectedModel().getShapes()) {
            if (shape.getType() != ShapeModel.Type.POLYGON)
                continue;

            List<Vector2> vs = shape.getVertices();

            for (int i = 0; i < vs.size(); i++) {
                Vector2 p1 = vs.get(i);
                Vector2 p2 = i != vs.size() - 1 ? vs.get(i + 1) : vs.get(0);

                if (selectedPoints.contains(p1) && selectedPoints.contains(p2)) {
                    Vector2 p = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
                    vs.add(i + 1, p);
                    toAdd.add(p);
                }
            }
        }

        selectedPoints.addAll(toAdd);
        Ctx.bodies.getSelectedModel().computePhysics();
    }

    public void removeSelectedPoints() {
        if (!isRemoveEnabled())
            return;

        List<ShapeModel> shapes = Ctx.bodies.getSelectedModel().getShapes();

        for (int i = shapes.size() - 1; i >= 0; i--) {
            ShapeModel shape = Ctx.bodies.getSelectedModel().getShapes().get(i);

            switch (shape.getType()) {
            case POLYGON:
                for (Vector2 p : selectedPoints) {
                    if (shape.getVertices().contains(p))
                        shape.getVertices().remove(p);
                }
                if (shape.getVertices().isEmpty())
                    shapes.remove(i);
                break;

            case CIRCLE:
                for (Vector2 p : selectedPoints) {
                    if (shape.getVertices().contains(p)) {
                        shapes.remove(i);
                        break;
                    }
                }
                break;
            }
        }

        selectedPoints.clear();
        Ctx.bodies.getSelectedModel().computePhysics();
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private void updateButtons() {
        RigidBodyModel model = Ctx.bodies.getSelectedModel();

        if (model != null) {
            lblSetImage.show();
            if (isImageValid())
                lblClearImage.show();
            else
                lblClearImage.hide();
            if (isImageValid() && model.getShapes().isEmpty())
                lblAutoTrace.show();
            else
                lblAutoTrace.hide();
            if (!model.getShapes().isEmpty())
                lblClearVertices.show();
            else
                lblClearVertices.hide();
            if (isRemoveEnabled())
                lblRemoveVertices.show();
            else
                lblRemoveVertices.hide();
            if (isInsertEnabled())
                lblInsertVertices.show();
            else
                lblInsertVertices.hide();

        } else {
            lblSetImage.hide();
            lblClearImage.hide();
            lblAutoTrace.hide();
            lblClearVertices.hide();
            lblInsertVertices.hide();
            lblRemoveVertices.hide();
        }
    }

    private void setMode(Mode mode) {
        this.mode = mode;

        canvas.input.removeProcessor(creationInputProcessor);
        canvas.input.removeProcessor(editionInputProcessor);
        canvas.input.removeProcessor(testInputProcessor);

        selectedPoints.clear();
        nextPoint = null;
        nearestPoint = null;

        RigidBodyModel model = Ctx.bodies.getSelectedModel();

        if (model != null) {

            if (!model.getShapes().isEmpty()) {
                if (!model.getShapes().get(model.getShapes().size() - 1).isClosed()) {
                    model.getShapes().remove(model.getShapes().size() - 1);
                }

            }

        }

        if (mode == null) {
            lblModeCreation.hide();
            lblModeEdition.hide();
            lblModeTest.hide();

        } else {
            lblModeCreation.hideSemi();
            lblModeEdition.hideSemi();
            lblModeTest.hideSemi();

            switch (mode) {
            case CREATION:
                lblModeCreation.show();
                canvas.input.addProcessor(creationInputProcessor);
                break;

            case EDITION:
                lblModeEdition.show();
                canvas.input.addProcessor(editionInputProcessor);
                break;

            case TEST:
                lblModeTest.show();
                canvas.input.addProcessor(testInputProcessor);
                break;
            }
        }
    }

    private void setNextMode() {
        Mode m = mode == Mode.CREATION ? Mode.EDITION : mode == Mode.EDITION ? Mode.TEST : Mode.CREATION;
        setMode(m);
    }

    private boolean isInsertEnabled() {
        RigidBodyModel model = Ctx.bodies.getSelectedModel();

        if (model == null)
            return false;
        if (selectedPoints.size() <= 1)
            return false;

        for (ShapeModel shape : model.getShapes()) {
            if (shape.getType() != ShapeModel.Type.POLYGON)
                continue;

            Vector2 v1 = null;
            for (Vector2 v2 : shape.getVertices()) {
                if (v1 != null && selectedPoints.contains(v2))
                    return true;
                v1 = selectedPoints.contains(v2) ? v2 : null;
            }
            if (v1 != null && selectedPoints.contains(shape.getVertices().get(0)))
                return true;
        }

        return false;
    }

    private boolean isRemoveEnabled() {
        if (Ctx.bodies.getSelectedModel() == null)
            return false;
        return !selectedPoints.isEmpty();
    }

    private boolean isImageValid() {
        RigidBodyModel model = Ctx.bodies.getSelectedModel();
        if (model == null)
            return false;
        if (model.getImagePath() == null || !model.isImagePathValid())
            return false;
        return true;
    }

    private void autoTrace() {
        if (!isImageValid())
            return;

        RigidBodyModel model = Ctx.bodies.getSelectedModel();
        File file = Ctx.io.getImageFile(model.getImagePath());
        Vector2[][] polygons = Tracer.trace(file.getPath(), Settings.autoTraceHullTolerance,
                Settings.autoTraceAlphaTolerance, Settings.autoTraceMultiPartDetection,
                Settings.autoTraceHoleDetection);

        if (polygons == null)
            return;

        for (Vector2[] polygon : polygons) {
            if (polygon.length < 3)
                continue;
            ShapeModel shape = new ShapeModel(ShapeModel.Type.POLYGON);
            shape.getVertices().addAll(Arrays.asList(polygon));
            shape.close();
            model.getShapes().add(shape);
        }

        buildBody();
        model.computePhysics();
    }

    private void clearPoints() {
        if (Ctx.bodies.getSelectedModel() == null)
            return;
        selectedPoints.clear();
        Ctx.bodies.getSelectedModel().clear();
    }

    private void clearWorld() {
        ballsBodies.clear();
        ballsSprites.clear();
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body b : bodies)
            world.destroyBody(b);
    }

    private void createBody() {
        RigidBodyModel model = Ctx.bodies.getSelectedModel();
        if (model == null)
            return;
        if (model.getPolygons().isEmpty() && model.getCircles().isEmpty())
            return;

        BodyDef bd = new BodyDef();
        bd.type = BodyType.StaticBody;

        Body body = world.createBody(bd);

        for (PolygonModel polygon : model.getPolygons()) {
            Vector2[] vs = polygon.vertices.toArray(new Vector2[0]);

            if (PolygonUtils.getPolygonArea(vs) < 0.00001f)
                continue;

            PolygonShape shape = new PolygonShape();
            shape.set(vs);

            FixtureDef fd = new FixtureDef();
            fd.density = 1f;
            fd.friction = 0.5f;
            fd.restitution = 1f;
            fd.shape = shape;

            body.createFixture(fd);
            shape.dispose();
        }

        for (CircleModel circle : model.getCircles()) {
            CircleShape shape = new CircleShape();
            shape.setPosition(circle.center);
            shape.setRadius(circle.radius);

            FixtureDef fd = new FixtureDef();
            fd.density = 1f;
            fd.friction = 0.5f;
            fd.restitution = 1f;
            fd.shape = shape;

            body.createFixture(fd);
            shape.dispose();
        }
    }

    private void createBodySprite(boolean isLoad) {

        RigidBodyModel model = Ctx.bodies.getSelectedModel();
        if (bodySprite != null) {
            // bodySprite.getTexture().dispose();

            Assets.inst().removeRegion(model);
        }

        bodySprite = null;

        if (model == null)
            return;

        TextureRegion region = Assets.inst().getRegion(model);

        if (region == null)
            return;

        bodySprite = new Sprite(region);
        bodySprite.setPosition(0, 0);
        bodySprite.setColor(1, 1, 1, 1f);

        bodySprite.setSize(bodySprite.getWidth(), bodySprite.getHeight());

        if (isLoad) {
            Ctx.bodies.getSelectedModel().getOrigin().x = bodySprite.getWidth() / 2;
            Ctx.bodies.getSelectedModel().getOrigin().y = bodySprite.getHeight() / 2;
        }

    }

    private void createBall(Vector2 orig, Vector2 force) {
        Random rand = new Random();
        float radius = rand.nextFloat() * 5.0f + 10.0f;

        BodyDef bd = new BodyDef();
        bd.type = BodyType.DynamicBody;
        bd.angularDamping = 0.3f;
        bd.linearDamping = 0.3f;
        bd.position.set(orig);
        bd.angle = rand.nextFloat() * MathUtils.PI;

        Body b = world.createBody(bd);
        b.applyLinearImpulse(force, orig, true);// TODO probably a bug

        ballsBodies.add(b);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fd = new FixtureDef();
        fd.density = 1f;
        fd.friction = 0.2f;
        fd.restitution = 10f;
        fd.shape = shape;

        b.createFixture(fd);

        Sprite sp = new Sprite(Assets.inst().get("data/ball.png", Texture.class));
        sp.setSize(radius * 2, radius * 2);
        sp.setOrigin(sp.getWidth() / 2, sp.getHeight() / 2);
        ballsSprites.add(sp);
    }

    private void resetWorld() {
        bodySprite = null;
        clearWorld();

        RigidBodyModel model = Ctx.bodies.getSelectedModel();
        if (model == null)
            return;

        createBody();

        createBodySprite(false);
    }
}
