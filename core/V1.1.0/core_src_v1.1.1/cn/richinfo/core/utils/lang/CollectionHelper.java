package cn.richinfo.core.utils.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* 集合工具类
*/
public class CollectionHelper {

	/**
	 * 判断一个集合对象是否为null或为空
	 * @param collection 要检测的集合对象
	 * @return 若集合对象为null或为空，则返回true；否则返回false
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * 判断一个集合对象是否不为null且不为空
	 * @param collection 要检测的集合对象
	 * @return 若集合对象不为null且不为空，则返回true；否则返回false
	 */
	public static boolean isNotEmpty(Collection<?> collection) {
		return !CollectionHelper.isEmpty(collection);
	}
	
	public static boolean isEmpty(Map<?, ?> map){
		return (map == null || map.isEmpty());
	}
	
	public static boolean isNotEmpty(Map<?, ?> map){
		return !CollectionHelper.isEmpty(map);
	}
	
	/**
	 * 将数组转为List
	 * 本方法不采用Arrays.asList()
	 * 避免Arrays.asList产生的list固定大小不可再add其他元素
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> List<T> arrayToList(T... array){
		List<T> list = new ArrayList<T>(array.length);
		Collections.addAll(list, array);
		return list;
	}

	/**
	 * 得到集合的第一个元素
	 * @param <T> 泛型
	 * @param collection 集合
	 * @return 集合的第一个元素，若集合为null，则返回null
	 */
	public static <T> T getFirst(Collection<T> collection) {
		return isEmpty(collection) ? null : collection.iterator().next();
	}
	
	/**
	 * @param <T> 泛型
	 * @param list  集合
	 * @param pageSize 每组记录数
	 * @return  分组后的集合组成新的集合
	 */
	public static <T> List<List<T>> groupList(List<T> list,int pageSize){
		List<List<T>> resultList = new ArrayList<List<T>>();
		if(isEmpty(list)) return resultList;
		int total = list.size();
		int totalPages = total%pageSize == 0? total/pageSize:total/pageSize+1;
		for(int i=1 ;i<=totalPages;i++){
			resultList.add(list.subList((i-1)*pageSize, (i*pageSize)<=total?(i*pageSize):total));
		}
		return resultList;
	}
	
}
