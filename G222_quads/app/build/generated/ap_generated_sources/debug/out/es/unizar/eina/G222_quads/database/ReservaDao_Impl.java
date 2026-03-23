package es.unizar.eina.G222_quads.database;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class ReservaDao_Impl implements ReservaDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Reserva> __insertAdapterOfReserva;

  private final EntityDeleteOrUpdateAdapter<Reserva> __deleteAdapterOfReserva;

  private final EntityDeleteOrUpdateAdapter<Reserva> __updateAdapterOfReserva;

  public ReservaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfReserva = new EntityInsertAdapter<Reserva>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `reserva` (`id`,`nombreCliente`,`movilCliente`,`fechaRecogida`,`horaRecogida`,`recogidaComparable`,`fechaDevolucion`,`horaDevolucion`,`devolucionComparable`,`precioTotal`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Reserva entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getNombreCliente() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getNombreCliente());
        }
        if (entity.getMovilCliente() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getMovilCliente());
        }
        statement.bindLong(4, entity.getFechaRecogida());
        final int _tmp = entity.getHoraRecogida() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getRecogidaComparable());
        statement.bindLong(7, entity.getFechaDevolucion());
        final int _tmp_1 = entity.getHoraDevolucion() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindLong(9, entity.getDevolucionComparable());
        statement.bindDouble(10, entity.getPrecioTotal());
      }
    };
    this.__deleteAdapterOfReserva = new EntityDeleteOrUpdateAdapter<Reserva>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reserva` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Reserva entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfReserva = new EntityDeleteOrUpdateAdapter<Reserva>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reserva` SET `id` = ?,`nombreCliente` = ?,`movilCliente` = ?,`fechaRecogida` = ?,`horaRecogida` = ?,`recogidaComparable` = ?,`fechaDevolucion` = ?,`horaDevolucion` = ?,`devolucionComparable` = ?,`precioTotal` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Reserva entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getNombreCliente() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getNombreCliente());
        }
        if (entity.getMovilCliente() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getMovilCliente());
        }
        statement.bindLong(4, entity.getFechaRecogida());
        final int _tmp = entity.getHoraRecogida() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getRecogidaComparable());
        statement.bindLong(7, entity.getFechaDevolucion());
        final int _tmp_1 = entity.getHoraDevolucion() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindLong(9, entity.getDevolucionComparable());
        statement.bindDouble(10, entity.getPrecioTotal());
        statement.bindLong(11, entity.getId());
      }
    };
  }

  @Override
  public long insert(final Reserva reserva) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfReserva.insertAndReturnId(_connection, reserva);
    });
  }

  @Override
  public int delete(final Reserva reserva) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      int _result = 0;
      _result += __deleteAdapterOfReserva.handle(_connection, reserva);
      return _result;
    });
  }

  @Override
  public int update(final Reserva reserva) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      int _result = 0;
      _result += __updateAdapterOfReserva.handle(_connection, reserva);
      return _result;
    });
  }

  @Override
  public LiveData<List<Reserva>> getReservasOrderByNombre() {
    final String _sql = "SELECT * FROM reserva ORDER BY nombreCliente ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNombreCliente = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nombreCliente");
        final int _columnIndexOfMovilCliente = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "movilCliente");
        final int _columnIndexOfFechaRecogida = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fechaRecogida");
        final int _columnIndexOfHoraRecogida = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "horaRecogida");
        final int _columnIndexOfRecogidaComparable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "recogidaComparable");
        final int _columnIndexOfFechaDevolucion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fechaDevolucion");
        final int _columnIndexOfHoraDevolucion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "horaDevolucion");
        final int _columnIndexOfDevolucionComparable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "devolucionComparable");
        final int _columnIndexOfPrecioTotal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precioTotal");
        final List<Reserva> _result = new ArrayList<Reserva>();
        while (_stmt.step()) {
          final Reserva _item;
          final String _tmpNombreCliente;
          if (_stmt.isNull(_columnIndexOfNombreCliente)) {
            _tmpNombreCliente = null;
          } else {
            _tmpNombreCliente = _stmt.getText(_columnIndexOfNombreCliente);
          }
          final String _tmpMovilCliente;
          if (_stmt.isNull(_columnIndexOfMovilCliente)) {
            _tmpMovilCliente = null;
          } else {
            _tmpMovilCliente = _stmt.getText(_columnIndexOfMovilCliente);
          }
          final long _tmpFechaRecogida;
          _tmpFechaRecogida = _stmt.getLong(_columnIndexOfFechaRecogida);
          final boolean _tmpHoraRecogida;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfHoraRecogida));
          _tmpHoraRecogida = _tmp != 0;
          final long _tmpFechaDevolucion;
          _tmpFechaDevolucion = _stmt.getLong(_columnIndexOfFechaDevolucion);
          final boolean _tmpHoraDevolucion;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfHoraDevolucion));
          _tmpHoraDevolucion = _tmp_1 != 0;
          _item = new Reserva(_tmpNombreCliente,_tmpMovilCliente,_tmpFechaRecogida,_tmpHoraRecogida,_tmpFechaDevolucion,_tmpHoraDevolucion);
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          _item.setId(_tmpId);
          final long _tmpRecogidaComparable;
          _tmpRecogidaComparable = _stmt.getLong(_columnIndexOfRecogidaComparable);
          _item.setRecogidaComparable(_tmpRecogidaComparable);
          final long _tmpDevolucionComparable;
          _tmpDevolucionComparable = _stmt.getLong(_columnIndexOfDevolucionComparable);
          _item.setDevolucionComparable(_tmpDevolucionComparable);
          final double _tmpPrecioTotal;
          _tmpPrecioTotal = _stmt.getDouble(_columnIndexOfPrecioTotal);
          _item.setPrecioTotal(_tmpPrecioTotal);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Reserva>> getReservasOrderByTelefono() {
    final String _sql = "SELECT * FROM reserva ORDER BY movilCliente ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNombreCliente = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nombreCliente");
        final int _columnIndexOfMovilCliente = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "movilCliente");
        final int _columnIndexOfFechaRecogida = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fechaRecogida");
        final int _columnIndexOfHoraRecogida = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "horaRecogida");
        final int _columnIndexOfRecogidaComparable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "recogidaComparable");
        final int _columnIndexOfFechaDevolucion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fechaDevolucion");
        final int _columnIndexOfHoraDevolucion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "horaDevolucion");
        final int _columnIndexOfDevolucionComparable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "devolucionComparable");
        final int _columnIndexOfPrecioTotal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precioTotal");
        final List<Reserva> _result = new ArrayList<Reserva>();
        while (_stmt.step()) {
          final Reserva _item;
          final String _tmpNombreCliente;
          if (_stmt.isNull(_columnIndexOfNombreCliente)) {
            _tmpNombreCliente = null;
          } else {
            _tmpNombreCliente = _stmt.getText(_columnIndexOfNombreCliente);
          }
          final String _tmpMovilCliente;
          if (_stmt.isNull(_columnIndexOfMovilCliente)) {
            _tmpMovilCliente = null;
          } else {
            _tmpMovilCliente = _stmt.getText(_columnIndexOfMovilCliente);
          }
          final long _tmpFechaRecogida;
          _tmpFechaRecogida = _stmt.getLong(_columnIndexOfFechaRecogida);
          final boolean _tmpHoraRecogida;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfHoraRecogida));
          _tmpHoraRecogida = _tmp != 0;
          final long _tmpFechaDevolucion;
          _tmpFechaDevolucion = _stmt.getLong(_columnIndexOfFechaDevolucion);
          final boolean _tmpHoraDevolucion;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfHoraDevolucion));
          _tmpHoraDevolucion = _tmp_1 != 0;
          _item = new Reserva(_tmpNombreCliente,_tmpMovilCliente,_tmpFechaRecogida,_tmpHoraRecogida,_tmpFechaDevolucion,_tmpHoraDevolucion);
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          _item.setId(_tmpId);
          final long _tmpRecogidaComparable;
          _tmpRecogidaComparable = _stmt.getLong(_columnIndexOfRecogidaComparable);
          _item.setRecogidaComparable(_tmpRecogidaComparable);
          final long _tmpDevolucionComparable;
          _tmpDevolucionComparable = _stmt.getLong(_columnIndexOfDevolucionComparable);
          _item.setDevolucionComparable(_tmpDevolucionComparable);
          final double _tmpPrecioTotal;
          _tmpPrecioTotal = _stmt.getDouble(_columnIndexOfPrecioTotal);
          _item.setPrecioTotal(_tmpPrecioTotal);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Reserva>> getReservasOrderByRecogida() {
    final String _sql = "SELECT * FROM reserva ORDER BY recogidaComparable ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNombreCliente = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nombreCliente");
        final int _columnIndexOfMovilCliente = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "movilCliente");
        final int _columnIndexOfFechaRecogida = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fechaRecogida");
        final int _columnIndexOfHoraRecogida = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "horaRecogida");
        final int _columnIndexOfRecogidaComparable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "recogidaComparable");
        final int _columnIndexOfFechaDevolucion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fechaDevolucion");
        final int _columnIndexOfHoraDevolucion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "horaDevolucion");
        final int _columnIndexOfDevolucionComparable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "devolucionComparable");
        final int _columnIndexOfPrecioTotal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precioTotal");
        final List<Reserva> _result = new ArrayList<Reserva>();
        while (_stmt.step()) {
          final Reserva _item;
          final String _tmpNombreCliente;
          if (_stmt.isNull(_columnIndexOfNombreCliente)) {
            _tmpNombreCliente = null;
          } else {
            _tmpNombreCliente = _stmt.getText(_columnIndexOfNombreCliente);
          }
          final String _tmpMovilCliente;
          if (_stmt.isNull(_columnIndexOfMovilCliente)) {
            _tmpMovilCliente = null;
          } else {
            _tmpMovilCliente = _stmt.getText(_columnIndexOfMovilCliente);
          }
          final long _tmpFechaRecogida;
          _tmpFechaRecogida = _stmt.getLong(_columnIndexOfFechaRecogida);
          final boolean _tmpHoraRecogida;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfHoraRecogida));
          _tmpHoraRecogida = _tmp != 0;
          final long _tmpFechaDevolucion;
          _tmpFechaDevolucion = _stmt.getLong(_columnIndexOfFechaDevolucion);
          final boolean _tmpHoraDevolucion;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfHoraDevolucion));
          _tmpHoraDevolucion = _tmp_1 != 0;
          _item = new Reserva(_tmpNombreCliente,_tmpMovilCliente,_tmpFechaRecogida,_tmpHoraRecogida,_tmpFechaDevolucion,_tmpHoraDevolucion);
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          _item.setId(_tmpId);
          final long _tmpRecogidaComparable;
          _tmpRecogidaComparable = _stmt.getLong(_columnIndexOfRecogidaComparable);
          _item.setRecogidaComparable(_tmpRecogidaComparable);
          final long _tmpDevolucionComparable;
          _tmpDevolucionComparable = _stmt.getLong(_columnIndexOfDevolucionComparable);
          _item.setDevolucionComparable(_tmpDevolucionComparable);
          final double _tmpPrecioTotal;
          _tmpPrecioTotal = _stmt.getDouble(_columnIndexOfPrecioTotal);
          _item.setPrecioTotal(_tmpPrecioTotal);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Reserva>> getReservasOrderByDevolucion() {
    final String _sql = "SELECT * FROM reserva ORDER BY devolucionComparable ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNombreCliente = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nombreCliente");
        final int _columnIndexOfMovilCliente = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "movilCliente");
        final int _columnIndexOfFechaRecogida = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fechaRecogida");
        final int _columnIndexOfHoraRecogida = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "horaRecogida");
        final int _columnIndexOfRecogidaComparable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "recogidaComparable");
        final int _columnIndexOfFechaDevolucion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fechaDevolucion");
        final int _columnIndexOfHoraDevolucion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "horaDevolucion");
        final int _columnIndexOfDevolucionComparable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "devolucionComparable");
        final int _columnIndexOfPrecioTotal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precioTotal");
        final List<Reserva> _result = new ArrayList<Reserva>();
        while (_stmt.step()) {
          final Reserva _item;
          final String _tmpNombreCliente;
          if (_stmt.isNull(_columnIndexOfNombreCliente)) {
            _tmpNombreCliente = null;
          } else {
            _tmpNombreCliente = _stmt.getText(_columnIndexOfNombreCliente);
          }
          final String _tmpMovilCliente;
          if (_stmt.isNull(_columnIndexOfMovilCliente)) {
            _tmpMovilCliente = null;
          } else {
            _tmpMovilCliente = _stmt.getText(_columnIndexOfMovilCliente);
          }
          final long _tmpFechaRecogida;
          _tmpFechaRecogida = _stmt.getLong(_columnIndexOfFechaRecogida);
          final boolean _tmpHoraRecogida;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfHoraRecogida));
          _tmpHoraRecogida = _tmp != 0;
          final long _tmpFechaDevolucion;
          _tmpFechaDevolucion = _stmt.getLong(_columnIndexOfFechaDevolucion);
          final boolean _tmpHoraDevolucion;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfHoraDevolucion));
          _tmpHoraDevolucion = _tmp_1 != 0;
          _item = new Reserva(_tmpNombreCliente,_tmpMovilCliente,_tmpFechaRecogida,_tmpHoraRecogida,_tmpFechaDevolucion,_tmpHoraDevolucion);
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          _item.setId(_tmpId);
          final long _tmpRecogidaComparable;
          _tmpRecogidaComparable = _stmt.getLong(_columnIndexOfRecogidaComparable);
          _item.setRecogidaComparable(_tmpRecogidaComparable);
          final long _tmpDevolucionComparable;
          _tmpDevolucionComparable = _stmt.getLong(_columnIndexOfDevolucionComparable);
          _item.setDevolucionComparable(_tmpDevolucionComparable);
          final double _tmpPrecioTotal;
          _tmpPrecioTotal = _stmt.getDouble(_columnIndexOfPrecioTotal);
          _item.setPrecioTotal(_tmpPrecioTotal);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteAll() {
    final String _sql = "DELETE FROM reserva";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void updatePrecio(final int reservaId, final double precio) {
    final String _sql = "UPDATE reserva SET precioTotal = ? WHERE id = ? ";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, precio);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, reservaId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
