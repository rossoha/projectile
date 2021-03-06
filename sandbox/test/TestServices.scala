/* Generated File */
import com.kyleu.projectile.services.database.JdbcDatabase
import com.kyleu.projectile.services.database.slick.SlickQueryService
import com.kyleu.projectile.util.tracing.TracingService
import scala.concurrent.ExecutionContext

object TestServices {
  private[this] implicit val ec: ExecutionContext = ExecutionContext.global
  val trace = TracingService.noop
  lazy val db = new JdbcDatabase("application", "database.application") {}
  lazy val slick = new SlickQueryService("test", db.source, 30, trace)

  lazy val bigRowService = new services.size.BigRowService(db, trace)
  lazy val bottomRowService = new services.b.BottomRowService(db, trace)
  lazy val smallRowService = new services.size.SmallRowService(db, trace)
  lazy val topRowService = new services.t.TopRowService(db, trace)
}
