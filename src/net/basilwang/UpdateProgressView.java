package net.basilwang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class UpdateProgressView extends View {
	private String text="100%";
	Paint paint = new Paint();

    public UpdateProgressView(Context context) {
    super(context);
       init(); 
    }
    public UpdateProgressView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    public UpdateProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init()
    {
    	paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
    }
    @Override
    protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Rect rect = new Rect();
	this.paint.getTextBounds(this.text, 0, this.text.length(), rect);
	int x = (getWidth() / 2) - rect.centerX();
	int y = 10;
	canvas.drawText(this.text, x, y, this.paint);
    }

}
