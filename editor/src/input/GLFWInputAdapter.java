package input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import editor.EditorGame;

public class GLFWInputAdapter extends InputAdapter
{
    public GLFWInputAdapter()
    {
        super();
    }
    boolean isDragging = false;
    float startX,startY;
    float deltaX,deltaY;
    @Override
    public boolean touchDown(int screenX , int screenY , int pointer , int button) {
        startX = screenX;
        startY = screenY;
        if(isDragging)
            EditorGame.i().natives.setPosition(screenX,screenY);

        return super.touchDown(screenX , screenY , pointer , button);
    }

    @Override
    public boolean touchUp(int screenX , int screenY , int pointer , int button) {
        isDragging = false;
        return super.touchUp(screenX , screenY , pointer , button);
    }

    @Override
    public boolean touchDragged(int screenX , int screenY , int pointer) {
        isDragging = true;
        if(Gdx.input.getY()<35&&Gdx.input.isButtonPressed(Input.Buttons.LEFT)){


//            deltaX = screenX - startX;
//            deltaY = screenY - startY;
            deltaX = Gdx.input.getDeltaX();
            deltaY = Gdx.input.getDeltaY();
            startX = screenX;
            startY = screenY;



            //EditorGame.getInstance().natives.setPosition((int)deltaX,0);
            deltaY=0;
            deltaX=0;
        }
        return super.touchDragged(screenX , screenY , pointer);
    }

    @Override
    public boolean mouseMoved(int screenX , int screenY) {

        return super.mouseMoved(screenX , screenY);
    }

}



