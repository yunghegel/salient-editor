package ui.widgets;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import core.systems.GizmoSystem;
import core.systems.SceneSystem;
import editor.Context;

public class RenderWidgetUI extends Actor
{

Stage stage;
RenderWidget renderWidget;
public PopupMenu rightClickMenu;
public RenderWidgetMenuInputProcessor inputProcessor;
public Menu menu;
public MenuItem translate;
public MenuItem rotate;
public MenuItem scale;

    {

        rightClickMenu = new PopupMenu();

        menu = new Menu("Transform");
        translate = new MenuItem("Translate");
        rotate = new MenuItem("Rotate");
        scale = new MenuItem("Scale");




        rightClickMenu.addItem(translate);
        rightClickMenu.addItem(rotate);
        rightClickMenu.addItem(scale);
    }

public RenderWidgetUI(RenderWidget renderWidget)
{
    this.renderWidget = renderWidget;
    //this.stage = renderWidget.parentStage;
    inputProcessor = new RenderWidgetMenuInputProcessor();
//    rightClickMenu.setListener(new PopupMenu.PopupMenuListener()
//    {
////        @Override
////        public void activeItemChanged(MenuItem newActiveItem , boolean changedByKeyboard) {
////            if (newActiveItem == translate) {
////                Context.getInstance().gizmoSystem.toggleTranslateTool();
////            } else if (newActiveItem == rotate) {
////                Context.getInstance().gizmoSystem.toggleRotateTool();
////            } else if (newActiveItem == scale) {
////                Context.getInstance().gizmoSystem.toggleRotateTool();
////            }
////        }
//
//
//    });

//    renderWidget.addRenderWidgetInputProcessor(inputProcessor);
//    GestureDetector gestureDetector = new GestureDetector(inputProcessor);
//    renderWidget.addRenderWidgetInputProcessor(gestureDetector);

    createListeners();
}

public void createListeners(){
   translate.addListener(new ChangeListener()
   {
       @Override
       public void changed(ChangeEvent event , Actor actor) {
           Context.getInstance().gizmoSystem.toggleTranslateTool();
       }
   });

    rotate.addListener(new ChangeListener()
    {
         @Override
         public void changed(ChangeEvent event , Actor actor) {
              Context.getInstance().gizmoSystem.toggleRotateTool();
         }
    });

    scale.addListener(new ChangeListener()
    {
         @Override
         public void changed(ChangeEvent event , Actor actor) {
              Context.getInstance().gizmoSystem.toggleScaleTool();
         }
    });


}


public class RenderWidgetMenuInputProcessor extends RenderWidgetInputProcessor implements GestureDetector.GestureListener
{


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    boolean open = false;

    @Override
    public boolean touchDown(int screenX , int screenY , int pointer , int button) {
        Vector2 vec = new Vector2(screenX, screenY);
//        vec.set(stage.stageToScreenCoordinates(vec));
//        if (button == 1) {
//            if (open) {
//                open = false;
//
//            } else {
//                rightClickMenu.showMenu(stage, vec.x, vec.y);
//                open = true;
//            }
//        }






        return false;
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX , float amountY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public boolean touchDown(float x , float y , int pointer , int button) {
        return false;
    }

    @Override
    public boolean tap(float x , float y , int count , int button) {
        if (count == 2) {
            if (button == 0) {
                Context.getInstance().objectPickingSystem.deselectObject();
            }
        }
        return false;
    }

    @Override
    public boolean longPress(float x , float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX , float velocityY , int button) {
        return false;
    }

    @Override
    public boolean pan(float x , float y , float deltaX , float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x , float y , int pointer , int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance , float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1 , Vector2 initialPointer2 , Vector2 pointer1 , Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

}

}
