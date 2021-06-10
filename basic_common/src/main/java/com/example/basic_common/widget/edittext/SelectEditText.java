package com.example.basic_common.widget.edittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**捕获编辑框光标位置
 * Description:
 * Created by WuQuan on 2021/4/9.
 */
@SuppressLint("AppCompatCustomView")
public class SelectEditText extends EditText {

    private int mLastPos = 0;
    private int mCurPos = 0;

    private EditTextSelectChange editTextSelectChange;

    public void setEditTextSelectChange(EditTextSelectChange editTextSelectChange) {
        this.editTextSelectChange = editTextSelectChange;
    }

    public SelectEditText(Context context) {
        super(context);
    }

    public SelectEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (this.editTextSelectChange != null) {
            mCurPos = selEnd;
            editTextSelectChange.change(mLastPos,mCurPos);
            mLastPos = mCurPos;
        }
    }

    /**
     * 编辑框光标改变监听接口
     */
    public interface EditTextSelectChange {

        void change(int lastPos, int curPos);
    }
}
