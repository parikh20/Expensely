package team16.cs307.expensetracker;

import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class ImageActivityTest {
    @Rule
    public ActivityTestRule<ImageActivity> imgActivityRule = new ActivityTestRule<ImageActivity>(ImageActivity.class);
    private ImageActivity imgActivity = null;
    Instrumentation.ActivityMonitor  monitor ;
    @Before
    public void setUp() throws Exception {
        imgActivity= imgActivityRule.getActivity();
    }
    @Test
    public void testLaunch(){
        View btnChoose = imgActivity.findViewById(R.id.ImageActivity_Choose);
        View btnUpload = imgActivity.findViewById(R.id.ImageActivity_Upload);
        View btnImgview = imgActivity.findViewById(R.id.ImageActivity_imgView);

        assertNotNull(btnChoose);
        assertNotNull(btnImgview);
        assertNotNull(btnUpload);
    }

    @After
    public void tearDown() throws Exception {
        imgActivity=null;
    }
}