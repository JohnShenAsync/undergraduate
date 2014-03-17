package net.basilwang.fresh;

import java.util.ArrayList;
import java.util.List;

import net.basilwang.dao.DAOHelper;
import net.basilwang.dao.IDAOService;
import android.content.Context;
import android.database.Cursor;

public class PointOfStructureService implements IDAOService {

	private DAOHelper daoHelper;

	public PointOfStructureService(Context context) {
		this.daoHelper = new DAOHelper(context);
	}

	public void save(PointOfStructure point) {
		String sql = "INSERT INTO PointOfStructure(Id,Name,Latitude,Longitude,Width,Height,Mode,MapId,color) VALUES (?,?,?,?,?,?,?,?,?)";
		Object[] bindArgs = { point.getId(), point.getName(),
				point.getLatitude(), point.getLongitude(), point.getWidth(),
				point.getHeight(), point.getMode() ,point.getMapId(),point.getColor()};
		daoHelper.insert(sql, bindArgs);
	}

	public List<PointOfStructure> getPoint(int id) {
		String sql = "SELECT * FROM PointOfStructure where mapId=?";
		String sa[]={String.valueOf(id)};
		Cursor result = daoHelper.query(sql, sa);
		List<PointOfStructure> list = new ArrayList<PointOfStructure>();
		while (result.moveToNext()) {
			PointOfStructure structure=new PointOfStructure();
			structure.setId(result.getInt(0));
			structure.setName(result.getString(1));
			structure.setLatitude(result.getDouble(2));
			structure.setLongitude(result.getDouble(3));
			structure.setWidth(result.getDouble(4));
			structure.setHeight(result.getDouble(5));
			structure.setMode(result.getInt(6));
			structure.setMapId(result.getInt(7));
			structure.setColor(result.getInt(8));
			list.add(structure);
		}
		daoHelper.closeDB();
		return list;
	}

	@Override
	public void deleteAccount() {
		// TODO Auto-generated method stub
	}

}
