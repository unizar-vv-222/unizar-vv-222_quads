package es.unizar.eina.G222_quads.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class QuadDao_Impl implements QuadDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Quad> __insertionAdapterOfQuad;

  private final EntityDeletionOrUpdateAdapter<Quad> __updateAdapterOfQuad;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByMatricula;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public QuadDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuad = new EntityInsertionAdapter<Quad>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `quad` (`matricula`,`tipo`,`precio`,`descripcion`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Quad entity) {
        if (entity.getMatricula() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getMatricula());
        }
        final Integer _tmp = entity.getTipo() == null ? null : (entity.getTipo() ? 1 : 0);
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, _tmp);
        }
        if (entity.getPrecio() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getPrecio());
        }
        if (entity.getDescripcion() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDescripcion());
        }
      }
    };
    this.__updateAdapterOfQuad = new EntityDeletionOrUpdateAdapter<Quad>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `quad` SET `matricula` = ?,`tipo` = ?,`precio` = ?,`descripcion` = ? WHERE `matricula` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Quad entity) {
        if (entity.getMatricula() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getMatricula());
        }
        final Integer _tmp = entity.getTipo() == null ? null : (entity.getTipo() ? 1 : 0);
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, _tmp);
        }
        if (entity.getPrecio() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getPrecio());
        }
        if (entity.getDescripcion() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDescripcion());
        }
        if (entity.getMatricula() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMatricula());
        }
      }
    };
    this.__preparedStmtOfDeleteByMatricula = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM quad WHERE matricula = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM quad";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Quad quad) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfQuad.insertAndReturnId(quad);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int update(final Quad quad) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __updateAdapterOfQuad.handle(quad);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int deleteByMatricula(final String matricula) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByMatricula.acquire();
    int _argIndex = 1;
    if (matricula == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, matricula);
    }
    try {
      __db.beginTransaction();
      try {
        final int _result = _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
        return _result;
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteByMatricula.release(_stmt);
    }
  }

  @Override
  public int deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    try {
      __db.beginTransaction();
      try {
        final int _result = _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
        return _result;
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Quad>> getQuadsOrderByMatricula() {
    final String _sql = "SELECT * FROM quad ORDER BY matricula ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"quad"}, false, new Callable<List<Quad>>() {
      @Override
      @Nullable
      public List<Quad> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatricula = CursorUtil.getColumnIndexOrThrow(_cursor, "matricula");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfPrecio = CursorUtil.getColumnIndexOrThrow(_cursor, "precio");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final List<Quad> _result = new ArrayList<Quad>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Quad _item;
            final String _tmpMatricula;
            if (_cursor.isNull(_cursorIndexOfMatricula)) {
              _tmpMatricula = null;
            } else {
              _tmpMatricula = _cursor.getString(_cursorIndexOfMatricula);
            }
            final Boolean _tmpTipo;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfTipo)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfTipo);
            }
            _tmpTipo = _tmp == null ? null : _tmp != 0;
            final Double _tmpPrecio;
            if (_cursor.isNull(_cursorIndexOfPrecio)) {
              _tmpPrecio = null;
            } else {
              _tmpPrecio = _cursor.getDouble(_cursorIndexOfPrecio);
            }
            final String _tmpDescripcion;
            if (_cursor.isNull(_cursorIndexOfDescripcion)) {
              _tmpDescripcion = null;
            } else {
              _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            }
            _item = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Quad>> getQuadsOrderByTipo() {
    final String _sql = "SELECT * FROM quad ORDER BY tipo ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"quad"}, false, new Callable<List<Quad>>() {
      @Override
      @Nullable
      public List<Quad> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatricula = CursorUtil.getColumnIndexOrThrow(_cursor, "matricula");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfPrecio = CursorUtil.getColumnIndexOrThrow(_cursor, "precio");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final List<Quad> _result = new ArrayList<Quad>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Quad _item;
            final String _tmpMatricula;
            if (_cursor.isNull(_cursorIndexOfMatricula)) {
              _tmpMatricula = null;
            } else {
              _tmpMatricula = _cursor.getString(_cursorIndexOfMatricula);
            }
            final Boolean _tmpTipo;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfTipo)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfTipo);
            }
            _tmpTipo = _tmp == null ? null : _tmp != 0;
            final Double _tmpPrecio;
            if (_cursor.isNull(_cursorIndexOfPrecio)) {
              _tmpPrecio = null;
            } else {
              _tmpPrecio = _cursor.getDouble(_cursorIndexOfPrecio);
            }
            final String _tmpDescripcion;
            if (_cursor.isNull(_cursorIndexOfDescripcion)) {
              _tmpDescripcion = null;
            } else {
              _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            }
            _item = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Quad>> getQuadsOrderByPrecio() {
    final String _sql = "SELECT * FROM quad ORDER BY precio ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"quad"}, false, new Callable<List<Quad>>() {
      @Override
      @Nullable
      public List<Quad> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatricula = CursorUtil.getColumnIndexOrThrow(_cursor, "matricula");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfPrecio = CursorUtil.getColumnIndexOrThrow(_cursor, "precio");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final List<Quad> _result = new ArrayList<Quad>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Quad _item;
            final String _tmpMatricula;
            if (_cursor.isNull(_cursorIndexOfMatricula)) {
              _tmpMatricula = null;
            } else {
              _tmpMatricula = _cursor.getString(_cursorIndexOfMatricula);
            }
            final Boolean _tmpTipo;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfTipo)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfTipo);
            }
            _tmpTipo = _tmp == null ? null : _tmp != 0;
            final Double _tmpPrecio;
            if (_cursor.isNull(_cursorIndexOfPrecio)) {
              _tmpPrecio = null;
            } else {
              _tmpPrecio = _cursor.getDouble(_cursorIndexOfPrecio);
            }
            final String _tmpDescripcion;
            if (_cursor.isNull(_cursorIndexOfDescripcion)) {
              _tmpDescripcion = null;
            } else {
              _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            }
            _item = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Quad>> getAvailableQuads(final long fechaInicio, final long fechaFin) {
    final String _sql = "SELECT * FROM quad q WHERE q.matricula NOT IN (SELECT rq.matriculaQuad FROM reserva_quad_cascos rq INNER JOIN reserva r ON r.id = rq.reservaId WHERE r.fechaRecogida < ? AND r.fechaDevolucion > ? )";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fechaFin);
    _argIndex = 2;
    _statement.bindLong(_argIndex, fechaInicio);
    return __db.getInvalidationTracker().createLiveData(new String[] {"quad", "reserva_quad_cascos",
        "reserva"}, false, new Callable<List<Quad>>() {
      @Override
      @Nullable
      public List<Quad> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatricula = CursorUtil.getColumnIndexOrThrow(_cursor, "matricula");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfPrecio = CursorUtil.getColumnIndexOrThrow(_cursor, "precio");
          final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
          final List<Quad> _result = new ArrayList<Quad>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Quad _item;
            final String _tmpMatricula;
            if (_cursor.isNull(_cursorIndexOfMatricula)) {
              _tmpMatricula = null;
            } else {
              _tmpMatricula = _cursor.getString(_cursorIndexOfMatricula);
            }
            final Boolean _tmpTipo;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfTipo)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfTipo);
            }
            _tmpTipo = _tmp == null ? null : _tmp != 0;
            final Double _tmpPrecio;
            if (_cursor.isNull(_cursorIndexOfPrecio)) {
              _tmpPrecio = null;
            } else {
              _tmpPrecio = _cursor.getDouble(_cursorIndexOfPrecio);
            }
            final String _tmpDescripcion;
            if (_cursor.isNull(_cursorIndexOfDescripcion)) {
              _tmpDescripcion = null;
            } else {
              _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            }
            _item = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Quad getQuadByMatricula(final String matricula) {
    final String _sql = "SELECT * FROM quad WHERE matricula = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (matricula == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, matricula);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfMatricula = CursorUtil.getColumnIndexOrThrow(_cursor, "matricula");
      final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
      final int _cursorIndexOfPrecio = CursorUtil.getColumnIndexOrThrow(_cursor, "precio");
      final int _cursorIndexOfDescripcion = CursorUtil.getColumnIndexOrThrow(_cursor, "descripcion");
      final Quad _result;
      if (_cursor.moveToFirst()) {
        final String _tmpMatricula;
        if (_cursor.isNull(_cursorIndexOfMatricula)) {
          _tmpMatricula = null;
        } else {
          _tmpMatricula = _cursor.getString(_cursorIndexOfMatricula);
        }
        final Boolean _tmpTipo;
        final Integer _tmp;
        if (_cursor.isNull(_cursorIndexOfTipo)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getInt(_cursorIndexOfTipo);
        }
        _tmpTipo = _tmp == null ? null : _tmp != 0;
        final Double _tmpPrecio;
        if (_cursor.isNull(_cursorIndexOfPrecio)) {
          _tmpPrecio = null;
        } else {
          _tmpPrecio = _cursor.getDouble(_cursorIndexOfPrecio);
        }
        final String _tmpDescripcion;
        if (_cursor.isNull(_cursorIndexOfDescripcion)) {
          _tmpDescripcion = null;
        } else {
          _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
        }
        _result = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
