package firepolo.headtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer
{
	private GLSurfaceView surfaceView;
	private boolean installRequested;
	private Session session;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		surfaceView = findViewById(R.id.surfaceView);
		surfaceView.setPreserveEGLContextOnPause(true);
		surfaceView.setEGLContextClientVersion(2);
		surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
		surfaceView.setRenderer(this);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		surfaceView.setWillNotDraw(false);

		installRequested = false;
	}

	@Override
	protected void onDestroy()
	{
		if (session != null)
		{
			session.close();
			session = null;
		}

		super.onDestroy();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (!CameraPermissionHelper.hasCameraPermission(this))
		{
			CameraPermissionHelper.requestCameraPermission(this);
			return;
		}

		try
		{
			if (session == null)
			{
				switch (ArCoreApk.getInstance().requestInstall(this, !installRequested))
				{
					case INSTALLED:
					{
						session = new Session(this);
						break;
					}
					case INSTALL_REQUESTED:
					{
						installRequested = true;
						break;
					}
				}
			}
		}
		catch (UnavailableUserDeclinedInstallationException ex)
		{
			Toast.makeText(this, "TODO: handle exception " + ex, Toast.LENGTH_LONG).show();
			return;
		}
		catch (Exception ex)
		{
			return;
		}

		try
		{
			session.resume();
		}
		catch (CameraNotAvailableException ex)
		{
			session = null;
			return;
		}

		surfaceView.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		if (session != null)
		{
			surfaceView.onPause();
			session.pause();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results)
	{
		if (!CameraPermissionHelper.hasCameraPermission(this))
		{
			Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
			if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this))
			{
				CameraPermissionHelper.launchPermissionSettings(this);
			}
			finish();
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		try
		{
			Frame frame = session.update();
			frame.getCamera().getPose().
		}
		catch (Exception ex)
		{
		}
	}
}