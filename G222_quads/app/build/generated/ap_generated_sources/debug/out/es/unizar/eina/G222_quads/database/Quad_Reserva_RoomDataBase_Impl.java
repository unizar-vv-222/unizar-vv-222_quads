package es.unizar.eina.G222_quads.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class Quad_Reserva_RoomDataBase_Impl extends Quad_Reserva_RoomDataBase {
  private volatile QuadDao _quadDao;

  private volatile ReservaDao _reservaDao;

  private volatile ReservaQuadCascosDao _reservaQuadCascosDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(7) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `quad` (`matricula` TEXT NOT NULL, `tipo` INTEGER NOT NULL, `precio` REAL NOT NULL, `descripcion` TEXT NOT NULL, PRIMARY KEY(`matricula`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reserva` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombreCliente` TEXT NOT NULL, `movilCliente` TEXT NOT NULL, `fechaRecogida` INTEGER NOT NULL, `horaRecogida` INTEGER NOT NULL, `recogidaComparable` INTEGER NOT NULL, `fechaDevolucion` INTEGER NOT NULL, `horaDevolucion` INTEGER NOT NULL, `devolucionComparable` INTEGER NOT NULL, `precioTotal` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reserva_quad_cascos` (`reservaId` INTEGER NOT NULL, `matriculaQuad` TEXT NOT NULL, `numCascos` INTEGER NOT NULL, `precioOriginal` REAL NOT NULL, PRIMARY KEY(`reservaId`, `matriculaQuad`), FOREIGN KEY(`reservaId`) REFERENCES `reserva`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`matriculaQuad`) REFERENCES `quad`(`matricula`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reserva_quad_cascos_reservaId` ON `reserva_quad_cascos` (`reservaId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reserva_quad_cascos_matriculaQuad` ON `reserva_quad_cascos` (`matriculaQuad`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '043dd7cd8a6ef96a4060607f3deafed7')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `quad`");
        db.execSQL("DROP TABLE IF EXISTS `reserva`");
        db.execSQL("DROP TABLE IF EXISTS `reserva_quad_cascos`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsQuad = new HashMap<String, TableInfo.Column>(4);
        _columnsQuad.put("matricula", new TableInfo.Column("matricula", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuad.put("tipo", new TableInfo.Column("tipo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuad.put("precio", new TableInfo.Column("precio", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuad.put("descripcion", new TableInfo.Column("descripcion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuad = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQuad = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQuad = new TableInfo("quad", _columnsQuad, _foreignKeysQuad, _indicesQuad);
        final TableInfo _existingQuad = TableInfo.read(db, "quad");
        if (!_infoQuad.equals(_existingQuad)) {
          return new RoomOpenHelper.ValidationResult(false, "quad(es.unizar.eina.G222_quads.database.Quad).\n"
                  + " Expected:\n" + _infoQuad + "\n"
                  + " Found:\n" + _existingQuad);
        }
        final HashMap<String, TableInfo.Column> _columnsReserva = new HashMap<String, TableInfo.Column>(10);
        _columnsReserva.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReserva.put("nombreCliente", new TableInfo.Column("nombreCliente", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReserva.put("movilCliente", new TableInfo.Column("movilCliente", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReserva.put("fechaRecogida", new TableInfo.Column("fechaRecogida", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReserva.put("horaRecogida", new TableInfo.Column("horaRecogida", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReserva.put("recogidaComparable", new TableInfo.Column("recogidaComparable", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReserva.put("fechaDevolucion", new TableInfo.Column("fechaDevolucion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReserva.put("horaDevolucion", new TableInfo.Column("horaDevolucion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReserva.put("devolucionComparable", new TableInfo.Column("devolucionComparable", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReserva.put("precioTotal", new TableInfo.Column("precioTotal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReserva = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReserva = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReserva = new TableInfo("reserva", _columnsReserva, _foreignKeysReserva, _indicesReserva);
        final TableInfo _existingReserva = TableInfo.read(db, "reserva");
        if (!_infoReserva.equals(_existingReserva)) {
          return new RoomOpenHelper.ValidationResult(false, "reserva(es.unizar.eina.G222_quads.database.Reserva).\n"
                  + " Expected:\n" + _infoReserva + "\n"
                  + " Found:\n" + _existingReserva);
        }
        final HashMap<String, TableInfo.Column> _columnsReservaQuadCascos = new HashMap<String, TableInfo.Column>(4);
        _columnsReservaQuadCascos.put("reservaId", new TableInfo.Column("reservaId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReservaQuadCascos.put("matriculaQuad", new TableInfo.Column("matriculaQuad", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReservaQuadCascos.put("numCascos", new TableInfo.Column("numCascos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReservaQuadCascos.put("precioOriginal", new TableInfo.Column("precioOriginal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReservaQuadCascos = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysReservaQuadCascos.add(new TableInfo.ForeignKey("reserva", "CASCADE", "NO ACTION", Arrays.asList("reservaId"), Arrays.asList("id")));
        _foreignKeysReservaQuadCascos.add(new TableInfo.ForeignKey("quad", "CASCADE", "NO ACTION", Arrays.asList("matriculaQuad"), Arrays.asList("matricula")));
        final HashSet<TableInfo.Index> _indicesReservaQuadCascos = new HashSet<TableInfo.Index>(2);
        _indicesReservaQuadCascos.add(new TableInfo.Index("index_reserva_quad_cascos_reservaId", false, Arrays.asList("reservaId"), Arrays.asList("ASC")));
        _indicesReservaQuadCascos.add(new TableInfo.Index("index_reserva_quad_cascos_matriculaQuad", false, Arrays.asList("matriculaQuad"), Arrays.asList("ASC")));
        final TableInfo _infoReservaQuadCascos = new TableInfo("reserva_quad_cascos", _columnsReservaQuadCascos, _foreignKeysReservaQuadCascos, _indicesReservaQuadCascos);
        final TableInfo _existingReservaQuadCascos = TableInfo.read(db, "reserva_quad_cascos");
        if (!_infoReservaQuadCascos.equals(_existingReservaQuadCascos)) {
          return new RoomOpenHelper.ValidationResult(false, "reserva_quad_cascos(es.unizar.eina.G222_quads.database.ReservaQuadCascos).\n"
                  + " Expected:\n" + _infoReservaQuadCascos + "\n"
                  + " Found:\n" + _existingReservaQuadCascos);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "043dd7cd8a6ef96a4060607f3deafed7", "8e3f3f421cf2673766a629cc2e5797a8");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "quad","reserva","reserva_quad_cascos");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `quad`");
      _db.execSQL("DELETE FROM `reserva`");
      _db.execSQL("DELETE FROM `reserva_quad_cascos`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(QuadDao.class, QuadDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReservaDao.class, ReservaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReservaQuadCascosDao.class, ReservaQuadCascosDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public QuadDao quadDao() {
    if (_quadDao != null) {
      return _quadDao;
    } else {
      synchronized(this) {
        if(_quadDao == null) {
          _quadDao = new QuadDao_Impl(this);
        }
        return _quadDao;
      }
    }
  }

  @Override
  public ReservaDao reservaDao() {
    if (_reservaDao != null) {
      return _reservaDao;
    } else {
      synchronized(this) {
        if(_reservaDao == null) {
          _reservaDao = new ReservaDao_Impl(this);
        }
        return _reservaDao;
      }
    }
  }

  @Override
  public ReservaQuadCascosDao reservaQuadCascosDao() {
    if (_reservaQuadCascosDao != null) {
      return _reservaQuadCascosDao;
    } else {
      synchronized(this) {
        if(_reservaQuadCascosDao == null) {
          _reservaQuadCascosDao = new ReservaQuadCascosDao_Impl(this);
        }
        return _reservaQuadCascosDao;
      }
    }
  }
}
