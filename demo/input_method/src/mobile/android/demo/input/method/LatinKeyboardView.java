package mobile.android.demo.input.method;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

public class LatinKeyboardView extends KeyboardView
{

	static final int KEYCODE_OPTIONS = -100;

	public LatinKeyboardView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

}
