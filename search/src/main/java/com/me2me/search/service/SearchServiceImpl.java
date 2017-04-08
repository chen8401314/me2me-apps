package com.me2me.search.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.me2me.common.web.Response;
import com.me2me.common.web.Specification;
import com.me2me.search.dto.ShowAssociatedWordDTO;
import com.me2me.user.service.UserService;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/27.
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private UserService userService;
	
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    
    @Autowired
    private ContentSearchService searchService;
    
    @Override
    public Response search(String keyword,int page,int pageSize,long uid,int isSearchFans) {
        if(isSearchFans == Specification.SearchType.FANS.index){
            return userService.searchFans(keyword, page, pageSize, uid);
        }else {
            return userService.search(keyword, page, pageSize, uid);
        }
    }

    @Override
    public Response assistant(String keyword) {
        return userService.assistant(keyword);
    }

    @Override
    public Response associatedWord(String keyword){
    	List<String> list = searchService.associateKeywordList(keyword, 10);
    	
    	ShowAssociatedWordDTO resultDTO = new ShowAssociatedWordDTO();
    	for(String k : list){
    		ShowAssociatedWordDTO.WordElement e = null;
    		if(!StringUtils.isEmpty(k)){
    			e = new ShowAssociatedWordDTO.WordElement();
    			e.setSearchWords(k);
    			resultDTO.getResult().add(e);
    		}
    	}
    	
    	return Response.success(resultDTO);
    }
    
    @Override
    public Response allSearch(long uid, String keyword, int searchType, int page, int pageSize){
    	return null;
    }
	
	@Override
	public int indexUserData(boolean fully) throws Exception {
		return searchService.indexUserData(fully);
	}

	@Override
	public int indexUgcData(boolean fully) throws Exception {
		return searchService.indexUgcData(fully);
	}

	@Override
	public int indexKingdomData(boolean fully) throws Exception {
		return searchService.indexKingdomData(fully);
	}

	@Override
	public int indexSearchHistory(boolean fully) throws Exception {
		return searchService.indexSearchHistory(fully);
	}
}
