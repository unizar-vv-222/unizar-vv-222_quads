package es.unizar.eina.G222_quads.database;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
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
public final class ReservaQuadCascosDao_Impl implements ReservaQuadCascosDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<ReservaQuadCascos> __insertAdapterOfReservaQuadCascos;

  public ReservaQuadCascosDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfReservaQuadCascos = new EntityInsertAdapter<ReservaQuadCascos>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `reserva_quad_cascos` (`reservaId`,`matriculaQuad`,`numCascos`,`precioOriginal`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final ReservaQuadCascos entity) {
        statement.bindLong(1, entity.getReservaId());
        if (entity.getMatriculaQuad() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getMatriculaQuad());
        }
        statement.bindLong(3, entity.getNumCascos());
        statement.bindDouble(4, entity.getPrecioOriginal());
      }
    };
  }

  @Override
  public void insert(final ReservaQuadCascos reservaQuadCascos) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfReservaQuadCascos.insert(_connection, reservaQuadCascos);
      return null;
    });
  }

  @Override
  public void insertAll(final List<ReservaQuadCascos> reservaQuadCascos) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfReservaQuadCascos.insert(_connection, reservaQuadCascos);
      return null;
    });
  }

  @Override
  public LiveData<List<ReservaQuadCascos>> getByReservaLive(final int reservaId) {
    final String _sql = "SELECT * FROM reserva_quad_cascos WHERE reservaId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"reserva_quad_cascos"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, reservaId);
        final int _columnIndexOfReservaId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "reservaId");
        final int _columnIndexOfMatriculaQuad = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "matriculaQuad");
        final int _columnIndexOfNumCascos = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "numCascos");
        final int _columnIndexOfPrecioOriginal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precioOriginal");
        final List<ReservaQuadCascos> _result = new ArrayList<ReservaQuadCascos>();
        while (_stmt.step()) {
          final ReservaQuadCascos _item;
          final int _tmpReservaId;
          _tmpReservaId = (int) (_stmt.getLong(_columnIndexOfReservaId));
          final String _tmpMatriculaQuad;
          if (_stmt.isNull(_columnIndexOfMatriculaQuad)) {
            _tmpMatriculaQuad = null;
          } else {
            _tmpMatriculaQuad = _stmt.getText(_columnIndexOfMatriculaQuad);
          }
          final int _tmpNumCascos;
          _tmpNumCascos = (int) (_stmt.getLong(_columnIndexOfNumCascos));
          final double _tmpPrecioOriginal;
          _tmpPrecioOriginal = _stmt.getDouble(_columnIndexOfPrecioOriginal);
          _item = new ReservaQuadCascos(_tmpReservaId,_tmpMatriculaQuad,_tmpNumCascos,_tmpPrecioOriginal);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<ReservaQuadCascos> getByReservaSync(final int reservaId) {
    final String _sql = "SELECT * FROM reserva_quad_cascos WHERE reservaId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, reservaId);
        final int _columnIndexOfReservaId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "reservaId");
        final int _columnIndexOfMatriculaQuad = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "matriculaQuad");
        final int _columnIndexOfNumCascos = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "numCascos");
        final int _columnIndexOfPrecioOriginal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "precioOriginal");
        final List<ReservaQuadCascos> _result = new ArrayList<ReservaQuadCascos>();
        while (_stmt.step()) {
          final ReservaQuadCascos _item;
          final int _tmpReservaId;
          _tmpReservaId = (int) (_stmt.getLong(_columnIndexOfReservaId));
          final String _tmpMatriculaQuad;
          if (_stmt.isNull(_columnIndexOfMatriculaQuad)) {
            _tmpMatriculaQuad = null;
          } else {
            _tmpMatriculaQuad = _stmt.getText(_columnIndexOfMatriculaQuad);
          }
          final int _tmpNumCascos;
          _tmpNumCascos = (int) (_stmt.getLong(_columnIndexOfNumCascos));
          final double _tmpPrecioOriginal;
          _tmpPrecioOriginal = _stmt.getDouble(_columnIndexOfPrecioOriginal);
          _item = new ReservaQuadCascos(_tmpReservaId,_tmpMatriculaQuad,_tmpNumCascos,_tmpPrecioOriginal);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public double getPrecioDiarioReserva(final int reservaId) {
    final String _sql = "SELECT SUM(precioOriginal) FROM reserva_quad_cascos WHERE reservaId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, reservaId);
        final double _result;
        if (_stmt.step()) {
          _result = _stmt.getDouble(0);
        } else {
          _result = 0.0;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteByReserva(final int reservaId) {
    final String _sql = "DELETE FROM reserva_quad_cascos WHERE reservaId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, reservaId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void delete(final int reservaId, final String matricula) {
    final String _sql = "DELETE FROM reserva_quad_cascos WHERE reservaId = ? AND matriculaQuad = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, reservaId);
        _argIndex = 2;
        if (matricula == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, matricula);
        }
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void updateNumCascos(final int reservaId, final String matricula, final int numCascos) {
    final String _sql = "UPDATE reserva_quad_cascos SET numCascos = ? WHERE reservaId = ? AND matriculaQuad = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, numCascos);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, reservaId);
        _argIndex = 3;
        if (matricula == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, matricula);
        }
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
