package smallville7123.example.taskbuilder;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

public class TextViewUtils {
    /**
     * changes dp size to px size.
     */
    protected static int dp2Px(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int toDP(Resources resources, float val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, resources.getDisplayMetrics());
    }

    public static class TextViewSize {
        boolean valid;
        boolean isPx;
        int px;
        boolean isSp;
        float sp;
    }

    public static TextViewSize new_TextViewSize(int px) {
        return new_TextViewSize(new TextViewSize(), px);
    }

    public static TextViewSize new_TextViewSize(TextViewSize s, int px) {
        s.valid = px != -1;
        if (s.valid) {
            s.isPx = true;
            s.px = px;
        }
        return s;
    }

    public static TextViewSize new_TextViewSize(float sp) {
        return new_TextViewSize(new TextViewSize(), sp);
    }

    public static TextViewSize new_TextViewSize(TextViewSize s, float sp) {
        s.valid = true;
        s.isSp = true;
        s.sp = sp;
        return s;
    }

    /**
     * obtains the textSize value or equivilant, from the given attribute, in a manner that ensures
     * correct scaling with TextView itself, since android provides different methods of obtaining
     * this value, it is not always obvious to the user how this value should be obtained
     * @return a TextViewSize object containing information about the current size
     */
    public static TextViewSize getTextSizeAttributesSuitableForTextView(TypedArray attributes, @StyleableRes int index) {
        return new_TextViewSize(attributes.getDimensionPixelSize(index, -1));
    }

    /**
     * obtains the textSize value or equivilant, from the given attribute, in a manner that ensures
     * correct scaling with TextView itself, since android provides different methods of obtaining
     * this value, it is not always obvious to the user how this value should be obtained
     * @param defVal default size in Scaled Pixels should the attribute not be declared/found
     * @return a TextViewSize object containing information about the current size
     */
    public static TextViewSize getTextSizeAttributesSuitableForTextView(TypedArray attributes, @StyleableRes int index, float defVal) {
        TextViewSize s = new_TextViewSize(attributes.getDimensionPixelSize(index, -1));
        if (!s.valid) s = new_TextViewSize(s, defVal);
        return s;
    }

    public static boolean textSizeAttributesIsValid(TextViewSize textSize) {
        return textSize.valid;
    }

    /**
     * uses the given TextViewSize to correctly set the text size for the given TextView object
     */
    public static void setTextSizeAttributesSuitableForTextView(TextView textView, TextViewSize textSize) {
        if (textSizeAttributesIsValid(textSize)) {
            if (textSize.isSp) textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.sp);
            else textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.px);
        }
    }

    public static void setTextSizeAttributesSuitableForTextView(TextView textView, float textSizeSp) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
    }

    public static void setTextSizeAttributesSuitableForTextView(TextView textView, int textSizePx) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx);
    }
}
