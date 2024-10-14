package net.miyataroid.miyatamagrimoire.core.helpers

import android.app.Activity
import com.google.ar.core.Session

class ARCoreSessionLifecycleHelperImpl(
    val features: Set<Session.Feature> = setOf()
) : ARCoreSessionLifecycleHelper {

    override var installRequested = false
    override var session: Session? = null
    override var activity: Activity? = null

    /**
     * Creating a session may fail. In this case, session will remain null, and this function will be
     * called with an exception.
     *
     * See
     * [the `Session` constructor](https://developers.google.com/ar/reference/java/com/google/ar/core/Session#Session(android.content.Context)
     * ) for more details.
     */
    override var exceptionCallback: ((Exception) -> Unit)? = null

    /**
     * Before `Session.resume()` is called, a session must be configured. Use
     * [`Session.configure`](https://developers.google.com/ar/reference/java/com/google/ar/core/Session#configure-config)
     * or
     * [`setCameraConfig`](https://developers.google.com/ar/reference/java/com/google/ar/core/Session#setCameraConfig-cameraConfig)
     */
    override var beforeSessionResume: ((Session) -> Unit)? = null

    /**
     * Attempts to create a session. If Google Play Services for AR is not installed or not up to
     * date, request installation.
     *
     * @return null when the session cannot be created due to a lack of the CAMERA permission or when
     * Google Play Services for AR is not installed or up to date, or when session creation fails for
     * any reason. In the case of a failure, [exceptionCallback] is invoked with the failure
     * exception.
     */
    override fun tryCreateSession(): Session? {
        return try {
            Session(activity, features)
        } catch (e: Exception) {
            exceptionCallback?.invoke(e)
            null
        }
    }
}
