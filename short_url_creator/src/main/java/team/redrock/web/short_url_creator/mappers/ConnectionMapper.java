package team.redrock.web.short_url_creator.mappers;

import org.apache.ibatis.annotations.*;
import team.redrock.web.short_url_creator.beens.NetConnection;

@Mapper
public interface ConnectionMapper {
    @Select("select oUrl from connections where cUrl = #{cUrl}")
    public String findOUrlByCUrl(@Param("cUrl")String  cUrl);

    @Select("select cUrl from connections where oUrl = #{oUrl}")
    public String findCUrlByOUrl(@Param("oUrl")String oUrl);

    @Select("select * from connections where oUrl = #{oUrl}")
    public NetConnection findAllByOUrl(@Param("oUrl")String oUrl);

    @Select("select * from connections where cUrl = #{cUrl}")
    public NetConnection findAllByCUrl(@Param("cUrl")String oUrl);

    @Insert("insert into connections (oUrl,cUrl,outTime) values (#{oUrl},#{cUrl},#{outTime})")
    public int insertConnection(NetConnection connection);

    @Delete("delete from connections where oUrl = #{oUrl}")
    public int deleteConnectionByOUrl(@Param("oUrl")String oUrl);

    @Delete("delete from connections where cUrl = #{cUrl}")
    public int deleteConnectionByCUrl(@Param("cUrl")String cUrl);
}
