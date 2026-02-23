package es.unizar.eina.G222_quads.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ReservaQuadCascosDao_Impl implements ReservaQuadCascosDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReservaQuadCascos> __insertionAdapterOfReservaQuadCascos;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByReserva;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  private final SharedSQLiteStatement __preparedStmtOfUpdateNumCascos;

  public ReservaQuadCascosDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReservaQuadCascos = new EntityInsertionAdapter<ReservaQuadCascos>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `reserva_quad_cascos` (`reservaId`,`matriculaQuad`,`numCascos`,`precioOriginal`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ReservaQuadCascos entity) {
        statement.bindLong(1, entity.getReservaId());
        if (entity.getMatriculaQuad() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getMatriculaQuad());
        }
        statement.bindLong(3, entity.getNumCascos());
        statement.bindDouble(4, entity.getPrecioOriginal());
      }
    };
    this.__preparedStmtOfDeleteByReserva = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reserva_quad_cascos WHERE reservaId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reserva_quad_cascos WHERE reservaId = ? AND matriculaQuad = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateNumCascos = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE reserva_quad_cascos SET numCascos = ? WHERE reservaId = ? AND matriculaQuad = ?";
        return _query;
      }
    };
  }

  @Override
  public void insert(final ReservaQuadCascos reservaQuadCascos) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfReservaQuadCascos.insert(reservaQuadCascos);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertAll(final List<ReservaQuadCascos> reservaQuadCascos) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfReservaQuadCascos.insert(reservaQuadCascos);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteByReserva(final int reservaId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByReserva.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, reservaId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteByReserva.release(_stmt);
    }
  }

  @Override
  public void delete(final int reservaId, final String matricula) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, reservaId);
    _argIndex = 2;
    if (matricula == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, matricula);
    }
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDelete.release(_stmt);
    }
  }

  @Override
  public void updateNumCascos(final int reservaId, final String matricula, final int numCascos) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateNumCascos.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, numCascos);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, reservaId);
    _argIndex = 3;
    if (matricula == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, matricula);
    }
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateNumCascos.release(_stmt);
    }
  }

  @Override
  public LiveData<List<ReservaQuadCascos>> getByReservaLive(final int reservaId) {
    final String _sql = "SELECT * FROM reserva_quad_cascos WHERE reservaId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, reservaId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva_quad_cascos"}, false, new Callable<List<ReservaQuadCascos>>() {
      @Override
      @Nullable
      public List<ReservaQuadCascos> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfReservaId = CursorUtil.getColumnIndexOrThrow(_cursor, "reservaId");
          final int _cursorIndexOfMatriculaQuad = CursorUtil.getColumnIndexOrThrow(_cursor, "matriculaQuad");
          final int _cursorIndexOfNumCascos = CursorUtil.getColumnIndexOrThrow(_cursor, "numCascos");
          final int _cursorIndexOfPrecioOriginal = CursorUtil.getColumnIndexOrThrow(_cursor, "precioOriginal");
          final List<ReservaQuadCascos> _result = new ArrayList<ReservaQuadCascos>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReservaQuadCascos _item;
            final int _tmpReservaId;
            _tmpReservaId = _cursor.getInt(_cursorIndexOfReservaId);
            final String _tmpMatriculaQuad;
            if (_cursor.isNull(_cursorIndexOfMatriculaQuad)) {
              _tmpMatriculaQuad = null;
            } else {
              _tmpMatriculaQuad = _cursor.getString(_cursorIndexOfMatriculaQuad);
            }
            final int _tmpNumCascos;
            _tmpNumCascos = _cursor.getInt(_cursorIndexOfNumCascos);
            final double _tmpPrecioOriginal;
            _tmpPrecioOriginal = _cursor.getDouble(_cursorIndexOfPrecioOriginal);
            _item = new ReservaQuadCascos(_tmpReservaId,_tmpMatriculaQuad,_tmpNumCascos,_tmpPrecioOriginal);
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
  public List<ReservaQuadCascos> getByReservaSync(final int reservaId) {
    final String _sql = "SELECT * FROM reserva_quad_cascos WHERE reservaId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, reservaId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfReservaId = CursorUtil.getColumnIndexOrThrow(_cursor, "reservaId");
      final int _cursorIndexOfMatriculaQuad = CursorUtil.getColumnIndexOrThrow(_cursor, "matriculaQuad");
      final int _cursorIndexOfNumCascos = CursorUtil.getColumnIndexOrThrow(_cursor, "numCascos");
      final int _cursorIndexOfPrecioOriginal = CursorUtil.getColumnIndexOrThrow(_cursor, "precioOriginal");
      final List<ReservaQuadCascos> _result = new ArrayList<ReservaQuadCascos>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ReservaQuadCascos _item;
        final int _tmpReservaId;
        _tmpReservaId = _cursor.getInt(_cursorIndexOfReservaId);
        final String _tmpMatriculaQuad;
        if (_cursor.isNull(_cursorIndexOfMatriculaQuad)) {
          _tmpMatriculaQuad = null;
        } else {
          _tmpMatriculaQuad = _cursor.getString(_cursorIndexOfMatriculaQuad);
        }
        final int _tmpNumCascos;
        _tmpNumCascos = _cursor.getInt(_cursorIndexOfNumCascos);
        final double _tmpPrecioOriginal;
        _tmpPrecioOriginal = _cursor.getDouble(_cursorIndexOfPrecioOriginal);
        _item = new ReservaQuadCascos(_tmpReservaId,_tmpMatriculaQuad,_tmpNumCascos,_tmpPrecioOriginal);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public double getPrecioDiarioReserva(final int reservaId) {
    final String _sql = "SELECT SUM(precioOriginal) FROM reserva_quad_cascos WHERE reservaId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, reservaId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final double _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getDouble(0);
      } else {
        _result = 0.0;
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
