package com.github.dreamrec;

import com.github.dreamrec.filters.Filter;
import com.github.dreamrec.gcomponent.GComponentFastModel;
import com.github.dreamrec.gcomponent.GComponentModel;
import com.github.dreamrec.gcomponent.GComponentSlowModel;
import com.github.dreamrec.gcomponent.GComponentView;

import java.awt.event.*;

/**
 *
 */
public class Factory {
    private static final double dZoomPlus  = Math.sqrt(2.0);
    private static final double dZoomMinus = 1.0/dZoomPlus;

    public static GComponentView getGComponentView(Model model, final Controller controller, Filter... filter) {
        GComponentView gComponentView;
        if (filter[0].divider() == Model.DIVIDER) {
            gComponentView = createSlowGComponent(model, controller, filter);
        } else if (filter[0].divider() == 1) {
            gComponentView = createFastGComponent(model, filter);
        } else {
            throw new UnsupportedOperationException("divider = " + filter[0].divider() + " .Shoud be 1 or " + Model.DIVIDER);
        }
        addGComponentListeners(gComponentView, controller);
        return gComponentView;
    }

    private static GComponentView createFastGComponent(Model model, Filter... filter) {
        GComponentView gComponentView;
        GComponentModel gModel = new GComponentFastModel(model, filter);
        gComponentView = new GComponentView(gModel);
        return gComponentView;
    }

    private static GComponentView createSlowGComponent(Model model, final Controller controller,Filter... filter) {
        GComponentView gComponentView;
        final GComponentSlowModel gModel = new GComponentSlowModel(model, filter);
        gComponentView = new GComponentView(gModel);
        gComponentView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                int newPosition = mouseEvent.getX() - gModel.getLeftIndent() - gModel.getCursorWidth()/2;
                controller.moveCursor(newPosition);
            }
        });
        return gComponentView;
    }

    private static void addGComponentListeners(final GComponentView gComponentView, final Controller controller) {
        gComponentView.addComponentListener(new ComponentAdapter() {
            @Override
                public void componentResized(ComponentEvent componentEvent) {
                GComponentModel gModel = gComponentView.getComponentModel();
                gModel.setYSize(componentEvent.getComponent().getHeight() - gModel.getTopIndent() - gModel.getBottomIndent());
                controller.changeXSize(componentEvent.getComponent().getWidth() - gModel.getLeftIndent() - gModel.getRightIndent());
            }
        });

        gComponentView.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                double zoom = 1.0;
                GComponentModel gModel = gComponentView.getComponentModel();
                if(rotation > 0)zoom = dZoomPlus;
                else { zoom = dZoomMinus; }
                gModel.setYZoom(gModel.getYZoom() * zoom);

                gComponentView.repaint();
            }
        });

    }

    public static GraphScrollBar getSlowGraphScrollBar(Model model, final Controller controller) {
        GraphScrollBar scrollBar = new GraphScrollBar(new ScrollBarModelAdapter(model));
        scrollBar.addScrollListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                controller.scrollSlowGraph(adjustmentEvent.getValue());
            }
        });
        return scrollBar;
    }
    
    public static Model getModel(ApplicationProperties applicationProperties){
        Model model = new Model();
        model.setXSize(applicationProperties.getXSize());
        return model;
    }

    static class ScrollBarModelAdapter implements GraphScrollBarModel {
        private Model model;

        public ScrollBarModelAdapter(Model model) {
            this.model = model;
        }

        public int graphSize() {
            return model.getSlowDataSize();
        }

        public int graphIndex() {
            return model.getSlowGraphIndex();
        }

        public int screenSize() {
            return model.getXSize();
        }
    }
}

