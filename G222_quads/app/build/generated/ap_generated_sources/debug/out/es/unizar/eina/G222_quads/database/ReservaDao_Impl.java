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
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ReservaDao_Impl implements ReservaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Reserva> __insertionAdapterOfReserva;

  private final EntityInsertionAdapter<Reserva> __insertionAdapterOfReserva_1;

  private final EntityDeletionOrUpdateAdapter<Reserva> __deletionAdapterOfReserva;

  private final EntityDeletionOrUpdateAdapter<Reserva> __updateAdapterOfReserva;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePrecio;

  public ReservaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReserva = new EntityInsertionAdapter<Reserva>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `reserva` (`id`,`nombreCliente`,`movilCliente`,`fechaRecogida`,`fechaDevolucion`,`precioTotal`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Reserva entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getNombreCliente() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getNombreCliente());
        }
        if (entity.getMovilCliente() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMovilCliente());
        }
        statement.bindLong(4, entity.getFechaRecogida());
        statement.bindLong(5, entity.getFechaDevolucion());
        if (entity.getPrecioTotal() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getPrecioTotal());
        }
      }
    };
    this.__insertionAdapterOfReserva_1 = new EntityInsertionAdapter<Reserva>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `reserva` (`id`,`nombreCliente`,`movilCliente`,`fechaRecogida`,`fechaDevolucion`,`precioTotal`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Reserva entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getNombreCliente() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getNombreCliente());
        }
        if (entity.getMovilCliente() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMovilCliente());
        }
        statement.bindLong(4, entity.getFechaRecogida());
        statement.bindLong(5, entity.getFechaDevolucion());
        if (entity.getPrecioTotal() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getPrecioTotal());
        }
      }
    };
    this.__deletionAdapterOfReserva = new EntityDeletionOrUpdateAdapter<Reserva>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reserva` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Reserva entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfReserva = new EntityDeletionOrUpdateAdapter<Reserva>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reserva` SET `id` = ?,`nombreCliente` = ?,`movilCliente` = ?,`fechaRecogida` = ?,`fechaDevolucion` = ?,`precioTotal` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Reserva entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getNombreCliente() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getNombreCliente());
        }
        if (entity.getMovilCliente() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMovilCliente());
        }
        statement.bindLong(4, entity.getFechaRecogida());
        statement.bindLong(5, entity.getFechaDevolucion());
        if (entity.getPrecioTotal() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getPrecioTotal());
        }
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reserva";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePrecio = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE reserva SET precioTotal = ? WHERE id = ? ";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Reserva reserva) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfReserva.insertAndReturnId(reserva);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertAndReturnId(final Reserva reserva) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfReserva_1.insertAndReturnId(reserva);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int delete(final Reserva reserva) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __deletionAdapterOfReserva.handle(reserva);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int update(final Reserva reserva) {
    __db.assertNotSuspendingTransaction();
    int _total = 0;
    __db.beginTransaction();
    try {
      _total += __updateAdapterOfReserva.handle(reserva);
      __db.setTransactionSuccessful();
      return _total;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public void updatePrecio(final int reservaId, final double precio) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePrecio.acquire();
    int _argIndex = 1;
    _stmt.bindDouble(_argIndex, precio);
    _argIndex = 2;
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
      __preparedStmtOfUpdatePrecio.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Reserva>> getReservasOrderByNombre() {
    final String _sql = "SELECT * FROM reserva ORDER BY nombreCliente ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva"}, false, new Callable<List<Reserva>>() {
      @Override
      @Nullable
      public List<Reserva> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombreCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCliente");
          final int _cursorIndexOfMovilCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "movilCliente");
          final int _cursorIndexOfFechaRecogida = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaRecogida");
          final int _cursorIndexOfFechaDevolucion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaDevolucion");
          final int _cursorIndexOfPrecioTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "precioTotal");
          final List<Reserva> _result = new ArrayList<Reserva>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reserva _item;
            final String _tmpNombreCliente;
            if (_cursor.isNull(_cursorIndexOfNombreCliente)) {
              _tmpNombreCliente = null;
            } else {
              _tmpNombreCliente = _cursor.getString(_cursorIndexOfNombreCliente);
            }
            final String _tmpMovilCliente;
            if (_cursor.isNull(_cursorIndexOfMovilCliente)) {
              _tmpMovilCliente = null;
            } else {
              _tmpMovilCliente = _cursor.getString(_cursorIndexOfMovilCliente);
            }
            final long _tmpFechaRecogida;
            _tmpFechaRecogida = _cursor.getLong(_cursorIndexOfFechaRecogida);
            final long _tmpFechaDevolucion;
            _tmpFechaDevolucion = _cursor.getLong(_cursorIndexOfFechaDevolucion);
            _item = new Reserva(_tmpNombreCliente,_tmpMovilCliente,_tmpFechaRecogida,_tmpFechaDevolucion);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Double _tmpPrecioTotal;
            if (_cursor.isNull(_cursorIndexOfPrecioTotal)) {
              _tmpPrecioTotal = null;
            } else {
              _tmpPrecioTotal = _cursor.getDouble(_cursorIndexOfPrecioTotal);
            }
            _item.setPrecioTotal(_tmpPrecioTotal);
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
  public LiveData<List<Reserva>> getReservasOrderByTelefono() {
    final String _sql = "SELECT * FROM reserva ORDER BY movilCliente ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva"}, false, new Callable<List<Reserva>>() {
      @Override
      @Nullable
      public List<Reserva> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombreCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCliente");
          final int _cursorIndexOfMovilCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "movilCliente");
          final int _cursorIndexOfFechaRecogida = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaRecogida");
          final int _cursorIndexOfFechaDevolucion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaDevolucion");
          final int _cursorIndexOfPrecioTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "precioTotal");
          final List<Reserva> _result = new ArrayList<Reserva>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reserva _item;
            final String _tmpNombreCliente;
            if (_cursor.isNull(_cursorIndexOfNombreCliente)) {
              _tmpNombreCliente = null;
            } else {
              _tmpNombreCliente = _cursor.getString(_cursorIndexOfNombreCliente);
            }
            final String _tmpMovilCliente;
            if (_cursor.isNull(_cursorIndexOfMovilCliente)) {
              _tmpMovilCliente = null;
            } else {
              _tmpMovilCliente = _cursor.getString(_cursorIndexOfMovilCliente);
            }
            final long _tmpFechaRecogida;
            _tmpFechaRecogida = _cursor.getLong(_cursorIndexOfFechaRecogida);
            final long _tmpFechaDevolucion;
            _tmpFechaDevolucion = _cursor.getLong(_cursorIndexOfFechaDevolucion);
            _item = new Reserva(_tmpNombreCliente,_tmpMovilCliente,_tmpFechaRecogida,_tmpFechaDevolucion);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Double _tmpPrecioTotal;
            if (_cursor.isNull(_cursorIndexOfPrecioTotal)) {
              _tmpPrecioTotal = null;
            } else {
              _tmpPrecioTotal = _cursor.getDouble(_cursorIndexOfPrecioTotal);
            }
            _item.setPrecioTotal(_tmpPrecioTotal);
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
  public LiveData<List<Reserva>> getReservasOrderByFechaRecogida() {
    final String _sql = "SELECT * FROM reserva ORDER BY fechaRecogida ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva"}, false, new Callable<List<Reserva>>() {
      @Override
      @Nullable
      public List<Reserva> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombreCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCliente");
          final int _cursorIndexOfMovilCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "movilCliente");
          final int _cursorIndexOfFechaRecogida = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaRecogida");
          final int _cursorIndexOfFechaDevolucion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaDevolucion");
          final int _cursorIndexOfPrecioTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "precioTotal");
          final List<Reserva> _result = new ArrayList<Reserva>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reserva _item;
            final String _tmpNombreCliente;
            if (_cursor.isNull(_cursorIndexOfNombreCliente)) {
              _tmpNombreCliente = null;
            } else {
              _tmpNombreCliente = _cursor.getString(_cursorIndexOfNombreCliente);
            }
            final String _tmpMovilCliente;
            if (_cursor.isNull(_cursorIndexOfMovilCliente)) {
              _tmpMovilCliente = null;
            } else {
              _tmpMovilCliente = _cursor.getString(_cursorIndexOfMovilCliente);
            }
            final long _tmpFechaRecogida;
            _tmpFechaRecogida = _cursor.getLong(_cursorIndexOfFechaRecogida);
            final long _tmpFechaDevolucion;
            _tmpFechaDevolucion = _cursor.getLong(_cursorIndexOfFechaDevolucion);
            _item = new Reserva(_tmpNombreCliente,_tmpMovilCliente,_tmpFechaRecogida,_tmpFechaDevolucion);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Double _tmpPrecioTotal;
            if (_cursor.isNull(_cursorIndexOfPrecioTotal)) {
              _tmpPrecioTotal = null;
            } else {
              _tmpPrecioTotal = _cursor.getDouble(_cursorIndexOfPrecioTotal);
            }
            _item.setPrecioTotal(_tmpPrecioTotal);
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
  public LiveData<List<Reserva>> getReservasOrderByFechaDevolucion() {
    final String _sql = "SELECT * FROM reserva ORDER BY fechaDevolucion ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva"}, false, new Callable<List<Reserva>>() {
      @Override
      @Nullable
      public List<Reserva> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNombreCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "nombreCliente");
          final int _cursorIndexOfMovilCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "movilCliente");
          final int _cursorIndexOfFechaRecogida = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaRecogida");
          final int _cursorIndexOfFechaDevolucion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaDevolucion");
          final int _cursorIndexOfPrecioTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "precioTotal");
          final List<Reserva> _result = new ArrayList<Reserva>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reserva _item;
            final String _tmpNombreCliente;
            if (_cursor.isNull(_cursorIndexOfNombreCliente)) {
              _tmpNombreCliente = null;
            } else {
              _tmpNombreCliente = _cursor.getString(_cursorIndexOfNombreCliente);
            }
            final String _tmpMovilCliente;
            if (_cursor.isNull(_cursorIndexOfMovilCliente)) {
              _tmpMovilCliente = null;
            } else {
              _tmpMovilCliente = _cursor.getString(_cursorIndexOfMovilCliente);
            }
            final long _tmpFechaRecogida;
            _tmpFechaRecogida = _cursor.getLong(_cursorIndexOfFechaRecogida);
            final long _tmpFechaDevolucion;
            _tmpFechaDevolucion = _cursor.getLong(_cursorIndexOfFechaDevolucion);
            _item = new Reserva(_tmpNombreCliente,_tmpMovilCliente,_tmpFechaRecogida,_tmpFechaDevolucion);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Double _tmpPrecioTotal;
            if (_cursor.isNull(_cursorIndexOfPrecioTotal)) {
              _tmpPrecioTotal = null;
            } else {
              _tmpPrecioTotal = _cursor.getDouble(_cursorIndexOfPrecioTotal);
            }
            _item.setPrecioTotal(_tmpPrecioTotal);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
