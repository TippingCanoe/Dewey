# Dewey

## Introduction

Dewey is an android widget designed to hold an unlimited number of tabs, making it ideal for allowing pagination through a list, hence it being named after The Dewey Decimal System.
Most other implementations of this style of widget don't recycle their child views, leading to performance issues even on modern devices for large data-sets.

A demo application included with this repository shows an example of this functionality;

![demonstration](https://github.com/TippingCanoe/Dewey/blob/master/demo.gif)

## Installing

1. Add the repository;

	``` groovy
	repositories {
		maven {
			url 'https://jitpack.io'
		}
	}
	```

2. Add the dependency;

	``` groovy
	dependencies {
		compile 'com.TippingCanoe.Dewey:library:0.0.1'
	}
	```

## Creating layout

The `com.tippingcanoe.dewey.Dewey` view is the core of this project. Simply add one to
your view;

``` xml
<com.tippingcanoe.dewey.Dewey
	android:layout_width="match_parent"
	android:layout_height="wrap_content" />
```

You can also customize certain properties of the view, more on what those properties control later;

``` xml
<RelativeLayout
	xmlns:android="http://schemas.android.com/apk/res/android"
	xmlns:custom="http://schemas.android.com/apk/res-auto"
	android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.tippingcanoe.dewey.Dewey
    	android:layout_width="match_parent"
    	android:layout_height="wrap_content"
    	custom:stripColor="@color/accent_material_light"
        custom:animationDurationMs="@integer/animation_duration_ms"
        custom:cloakColor="@color/background_floating_material_dark"
        custom:minCloakPercentage="@integer/min_cloak_percentage"
        custom:stripHeight="@dimen/strip_height" />

</RelativeLayout>
```

## Setting up views

Once you've got your layout setup, you'll need to find your view;

``` java
Dewey dewey = (Dewey) findViewById(R.id.dewey);
```

Dewey extends `android.support.v7.widget.RecyclerView`, so you'll need to create and add a new `RecyclerView.Adapter`.
This works the same as any other implementation, so take a look at the example code in the `:app` module of this
repository or familiarize yourself with [Google's Documentation](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html);

``` java
dewey.setAdapter(new MyAdapter());

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
	...
}

public class MyViewHolder extends RecyclerView.ViewHolder {
	...
}
```

You can also setup a listener on the view to receive notifications when a new page is selected;

``` java
dewey.setOnFocusedPositionChangedListener(new Dewey.OnFocusedPositionChangedListener() {
	@Override
	public void onFocusedPositionChanged ( int previousFocusedPosition, int newFocusedPosition ) {
		...
	}
});
```

## Customizing

This library provides plenty of customization touch points to ensure the effect is right for your application;

* cloakColor
	* The first and last "page" of the list are always visible to the user, pinned at the beginning and end of the list. The "cloak" is rendered behind them to ensure they are legible.
* minCloakPercentage
	* Sets the minimum amount, between 0 and 100 percent, of the background "cloak"s transparency. From this point, it is calculated on a curve.
* stripHeight
	* Sets the height dimension for the current page indication strip.
* stripColor
	* Sets the colour for the current page indication strip.
* animationDurationMs
	* Sets the speed of the animation for changing pages, in milliseconds.

## Contact

Love it? Hate it? Want to make changes to it? Contact me at [@iainconnor](http://www.twitter.com/iainconnor) or
[iainconnor@gmail.com](mailto:iainconnor@gmail.com).
