package com.wangzy.httpmodel;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {



    @Test
    public void useAppContext() throws Exception {


        String json="{\"a\":{\"b\":\"vvvv\"}}";
        String ret=JsonSelector.getJsonObject(json,"a->b");
        Log.i("t",ret);


        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.wangzy.httpmodel", appContext.getPackageName());
    }
}
