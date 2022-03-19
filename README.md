# LoadingButton

一个带ProgressBar的Button，通过自定义ConstraintLayout实现。

<img src="https://github.com/laxian/LoadingButton/raw/main/images/record.gif" width="324" height="702" />

## 使用示例

布局方式：

```xml
    <com.zwx.loadingbutton.LoadingButton
        android:id="@+id/forth"
        android:layout_width="120dp"
        android:layout_height="40dp"
        android:layout_marginBottom="24dp"
        android:background="@drawable/mdw_btn_bg_round_gradient_enable_big"
        app:pbYesStyle="true"
        android:paddingHorizontal="12dp"
        app:pbIndeterminateDrawable="@drawable/mdw_progressbar_circle"
        app:pbLoadingText="LOGIN"
        app:pbProgressColor="#582707"
        app:pbProgressLeft="true"
        app:pbText="EXACTLY"
        app:pbTextColor="#7286A0"
        app:pbTextSize="16sp" />
```

代码方式：

```Java
        LoadingButton button = new LoadingButton(this);
        LoadingButton.ViewSwitcherFactory factory = new LoadingButton.ViewSwitcherFactory(this,
                getResources().getColor(android.R.color.white),
                44F,
                Typeface.DEFAULT);
        button.setTextFactory(factory);

        button.setProgressBarLeftMode();
        button.setText("new LoadingButton()");
        button.setLoadingText("wait...");
        button.setBackgroundColor(Color.RED);
        button.setOnClickListener(this);
        button.setPadding(0,20,0,20);
        button.setAnimationInDirection(LoadingButton.IN_FROM_LEFT);

        ((ViewGroup) findViewById(R.id.root)).addView(button);
```

## 新增自定义项

| 自定义项                | 说明                          |
| ----------------------- | ----------------------------- |
| pbText                  | 文字                          |
| pbLoadingText           | 加载中文字                    |
| pbTextSize              | 文字大小                      |
| pbTextColor             | 文字颜色                      |
| pbProgressColor         | 加载圈颜色                    |
| pbIndeterminateDrawable | 加载圈图                      |
| pbYesStyle              | 提供一种yes状态，在选择器使用 |
| pbWeakStyle             | 提供一种若样式，在选择器使用  |
| pbProgressSize          | 加载圈大小，默认等于文字      |
| pbProgressLeft          | 加载圈显示在左边，默认右边    |

## 实现方式

通过自定义ConstraintLayout实现。

通过RelativeLayout方式的实现，有一些缺陷：

- 宽度为wrap_content时，toEndOf，会出现位置不准。

- 宽度为wrap_content时，内部元素alignParentEnd，会导致wrap_content失效

## REF

修改自[snadjafi](https://github.com/snadjafi/LoadingButton)
