package es.unizar.eina.g222_quads.database;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteConnectionUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class QuadDao_Impl implements QuadDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Quad> __insertAdapterOfQuad;

  private final EntityDeleteOrUpdateAdapter<Quad> __updateAdapterOfQuad;

  public QuadDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfQuad = new EntityInsertAdapter<Quad>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `quad` (`matricula`,`tipo`,`precio`,`descripcion`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Quad entity) {
        if (entity.getMatricula() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getMatricula());
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
          statement.bindText(4, entity.getDescripcion());
        }
      }
    };
    this.__updateAdapterOfQuad = new EntityDeleteOrUpdateAdapter<Quad>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `quad` SET `matricula` = ?,`tipo` = ?,`precio` = ?,`descripcion` = ? WHERE `matricula` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Quad entity) {
        if (entity.getMatricula() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getMatricula());
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
          statement.bindText(4, entity.getDescripcion());
        }
        if (entity.getMatricula() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getMatricula());
        }
      }
    };
  }

  @Override
  public long insert(final Quad quad) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfQuad.insertAndReturnId(_connection, quad);
    });
  }

  @Override
  public int update(final Quad quad) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      int _result = 0;
      _result += __updateAdapterOfQuad.handle(_connection, quad);
      return _result;
    });
  }

  @Override
  public LiveData<List<Quad>> getQuadsOrderByMatricula() {
    final String _sql = "SELECT * FROM quad ORDER BY matricula ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"quad"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfMatricula = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "matricula");
        final int _columnIndexOfTipo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "tipo");
        final int _columnIndexOfPrecio = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precio");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final List<Quad> _result = new ArrayList<Quad>();
        while (_stmt.step()) {
          final Quad _item;
          final String _tmpMatricula;
          if (_stmt.isNull(_columnIndexOfMatricula)) {
            _tmpMatricula = null;
          } else {
            _tmpMatricula = _stmt.getText(_columnIndexOfMatricula);
          }
          final Boolean _tmpTipo;
          final Integer _tmp;
          if (_stmt.isNull(_columnIndexOfTipo)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(_columnIndexOfTipo));
          }
          _tmpTipo = _tmp == null ? null : _tmp != 0;
          final Double _tmpPrecio;
          if (_stmt.isNull(_columnIndexOfPrecio)) {
            _tmpPrecio = null;
          } else {
            _tmpPrecio = _stmt.getDouble(_columnIndexOfPrecio);
          }
          final String _tmpDescripcion;
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _tmpDescripcion = null;
          } else {
            _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion);
          }
          _item = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Quad>> getQuadsOrderByTipo() {
    final String _sql = "SELECT * FROM quad ORDER BY tipo ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"quad"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfMatricula = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "matricula");
        final int _columnIndexOfTipo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "tipo");
        final int _columnIndexOfPrecio = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precio");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final List<Quad> _result = new ArrayList<Quad>();
        while (_stmt.step()) {
          final Quad _item;
          final String _tmpMatricula;
          if (_stmt.isNull(_columnIndexOfMatricula)) {
            _tmpMatricula = null;
          } else {
            _tmpMatricula = _stmt.getText(_columnIndexOfMatricula);
          }
          final Boolean _tmpTipo;
          final Integer _tmp;
          if (_stmt.isNull(_columnIndexOfTipo)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(_columnIndexOfTipo));
          }
          _tmpTipo = _tmp == null ? null : _tmp != 0;
          final Double _tmpPrecio;
          if (_stmt.isNull(_columnIndexOfPrecio)) {
            _tmpPrecio = null;
          } else {
            _tmpPrecio = _stmt.getDouble(_columnIndexOfPrecio);
          }
          final String _tmpDescripcion;
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _tmpDescripcion = null;
          } else {
            _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion);
          }
          _item = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Quad>> getQuadsOrderByPrecio() {
    final String _sql = "SELECT * FROM quad ORDER BY precio ASC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"quad"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfMatricula = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "matricula");
        final int _columnIndexOfTipo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "tipo");
        final int _columnIndexOfPrecio = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precio");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final List<Quad> _result = new ArrayList<Quad>();
        while (_stmt.step()) {
          final Quad _item;
          final String _tmpMatricula;
          if (_stmt.isNull(_columnIndexOfMatricula)) {
            _tmpMatricula = null;
          } else {
            _tmpMatricula = _stmt.getText(_columnIndexOfMatricula);
          }
          final Boolean _tmpTipo;
          final Integer _tmp;
          if (_stmt.isNull(_columnIndexOfTipo)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(_columnIndexOfTipo));
          }
          _tmpTipo = _tmp == null ? null : _tmp != 0;
          final Double _tmpPrecio;
          if (_stmt.isNull(_columnIndexOfPrecio)) {
            _tmpPrecio = null;
          } else {
            _tmpPrecio = _stmt.getDouble(_columnIndexOfPrecio);
          }
          final String _tmpDescripcion;
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _tmpDescripcion = null;
          } else {
            _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion);
          }
          _item = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Quad>> getAvailableQuads(final long recogidaComparable,
      final long devolucionComparable) {
    final String _sql = "SELECT * FROM quad q WHERE q.matricula NOT IN (SELECT rq.matriculaQuad FROM reserva_quad_cascos rq INNER JOIN reserva r ON r.id = rq.reservaId WHERE r.recogidaComparable < ? AND r.devolucionComparable > ? )";
    return __db.getInvalidationTracker().createLiveData(new String[] {"quad", "reserva_quad_cascos",
        "reserva"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, devolucionComparable);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, recogidaComparable);
        final int _columnIndexOfMatricula = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "matricula");
        final int _columnIndexOfTipo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "tipo");
        final int _columnIndexOfPrecio = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precio");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final List<Quad> _result = new ArrayList<Quad>();
        while (_stmt.step()) {
          final Quad _item;
          final String _tmpMatricula;
          if (_stmt.isNull(_columnIndexOfMatricula)) {
            _tmpMatricula = null;
          } else {
            _tmpMatricula = _stmt.getText(_columnIndexOfMatricula);
          }
          final Boolean _tmpTipo;
          final Integer _tmp;
          if (_stmt.isNull(_columnIndexOfTipo)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(_columnIndexOfTipo));
          }
          _tmpTipo = _tmp == null ? null : _tmp != 0;
          final Double _tmpPrecio;
          if (_stmt.isNull(_columnIndexOfPrecio)) {
            _tmpPrecio = null;
          } else {
            _tmpPrecio = _stmt.getDouble(_columnIndexOfPrecio);
          }
          final String _tmpDescripcion;
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _tmpDescripcion = null;
          } else {
            _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion);
          }
          _item = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Quad>> getAvailableQuadsExcludingReserva(final long recogidaComparable,
      final long devolucionComparable, final int reservaId) {
    final String _sql = "SELECT * FROM quad q WHERE q.matricula NOT IN (SELECT rq.matriculaQuad FROM reserva_quad_cascos rq INNER JOIN reserva r ON r.id = rq.reservaId WHERE r.id != ? AND r.recogidaComparable < ? AND r.devolucionComparable > ? ) ";
    return __db.getInvalidationTracker().createLiveData(new String[] {"quad", "reserva_quad_cascos",
        "reserva"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, reservaId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, devolucionComparable);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, recogidaComparable);
        final int _columnIndexOfMatricula = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "matricula");
        final int _columnIndexOfTipo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "tipo");
        final int _columnIndexOfPrecio = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precio");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final List<Quad> _result = new ArrayList<Quad>();
        while (_stmt.step()) {
          final Quad _item;
          final String _tmpMatricula;
          if (_stmt.isNull(_columnIndexOfMatricula)) {
            _tmpMatricula = null;
          } else {
            _tmpMatricula = _stmt.getText(_columnIndexOfMatricula);
          }
          final Boolean _tmpTipo;
          final Integer _tmp;
          if (_stmt.isNull(_columnIndexOfTipo)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(_columnIndexOfTipo));
          }
          _tmpTipo = _tmp == null ? null : _tmp != 0;
          final Double _tmpPrecio;
          if (_stmt.isNull(_columnIndexOfPrecio)) {
            _tmpPrecio = null;
          } else {
            _tmpPrecio = _stmt.getDouble(_columnIndexOfPrecio);
          }
          final String _tmpDescripcion;
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _tmpDescripcion = null;
          } else {
            _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion);
          }
          _item = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Quad getQuadByMatricula(final String matricula) {
    final String _sql = "SELECT * FROM quad WHERE matricula = ? LIMIT 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (matricula == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, matricula);
        }
        final int _columnIndexOfMatricula = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "matricula");
        final int _columnIndexOfTipo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "tipo");
        final int _columnIndexOfPrecio = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precio");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final Quad _result;
        if (_stmt.step()) {
          final String _tmpMatricula;
          if (_stmt.isNull(_columnIndexOfMatricula)) {
            _tmpMatricula = null;
          } else {
            _tmpMatricula = _stmt.getText(_columnIndexOfMatricula);
          }
          final Boolean _tmpTipo;
          final Integer _tmp;
          if (_stmt.isNull(_columnIndexOfTipo)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(_columnIndexOfTipo));
          }
          _tmpTipo = _tmp == null ? null : _tmp != 0;
          final Double _tmpPrecio;
          if (_stmt.isNull(_columnIndexOfPrecio)) {
            _tmpPrecio = null;
          } else {
            _tmpPrecio = _stmt.getDouble(_columnIndexOfPrecio);
          }
          final String _tmpDescripcion;
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _tmpDescripcion = null;
          } else {
            _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion);
          }
          _result = new Quad(_tmpMatricula,_tmpTipo,_tmpPrecio,_tmpDescripcion);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public int deleteByMatricula(final String matricula) {
    final String _sql = "DELETE FROM quad WHERE matricula = ?";
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (matricula == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, matricula);
        }
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public int deleteAll() {
    final String _sql = "DELETE FROM quad";
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return SQLiteConnectionUtil.getTotalChangedRows(_connection);
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
