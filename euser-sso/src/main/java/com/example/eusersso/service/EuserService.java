package com.example.eusersso.service;

import com.example.eusersso.converter.EuserConverter;
import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dao.param.EuserSelectCond;
import com.example.eusersso.dto.request.UpdateEuserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.PageResp;
import com.example.eusersso.entity.AvuePermission;
import com.example.eusersso.entity.AvueRole;
import com.example.eusersso.mapper.AvueRoleMapper;
import com.example.eusersso.mapper.EuserMapper;
import com.example.eusersso.repository.AvueRoleRepository;
import com.example.eusersso.repository.EuserTotalRepository;
import com.example.eusersso.util.PasswordEncoder;
import com.example.eusersso.util.SubsystemEnum;
import com.example.eusersso.util.TimestampUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EuserService {

    private final EuserMapper euserMapper;

    private final AvueRoleMapper avueRoleMapper;

    private final PasswordEncoder passwordEncoder;

    private final EuserConverter euserConverter;

    private final AvueRoleRepository avueRoleRepository;

    private final EuserTotalRepository euserTotalRepository;

    @Transactional
    public int insertOne(EuserDao euserDao) {
        euserDao.setPassword(passwordEncoder.encode(euserDao.getPassword()));
        euserDao.setCreateTime(TimestampUtil.now());  // 获取自1970年1月1日以来的秒数
        return euserMapper.insertOne(euserDao);
    }

    @Transactional
    public PageResp<EuserListItem> selectPageByCond(String username, String screenName, Integer roleId, String routePath,
                                     Integer pageNum, Integer pageSize,
                                     SubsystemEnum subsystem
    ) {
        var cond = new EuserSelectCond();
        cond.setUsername(prepostParam(username));
        cond.setScreenName(prepostParam(screenName));
        cond.setAvueRoleId(roleId);
        cond.setApiId(routePath);
        cond.setDbAccessLabel(subsystem.getDbAccessLabel());
        cond.setPageSize(pageSize);
        cond.setPageNum(pageNum);
        var list = selectByCond(cond);
        PageResp<EuserListItem> page = new PageResp<>();
        page.setList(list);
        page.setPageNum(pageNum);
        page.setPageSize(list.size());
        page.setTotal(euserTotalRepository.getEuserTotal(cond, subsystem));
        return page;
    }

    public List<EuserListItem> selectByCond(EuserSelectCond cond) {
        return euserMapper.selectByCond(cond).stream().map(euserDao -> {
            EuserListItem item = euserConverter.toEuserListItem(euserDao);
            item.setAvueRoles(avueRoleRepository.getRoleNames(euserDao.getAvueRoles()));
            return item;
        }).toList();
    }

    public int updateUser(EuserDao euserDao) {
        return euserMapper.updateUser(euserDao);
    }

    public int deleteByUsername(String username) {
        return euserMapper.deleteByUsername(username);
    }

    public EuserDao selectOneSimple(String username) {
        return euserMapper.selectByUsername(username);
    }

    public EuserDao selectByMobile(String mobile) {
        return euserMapper.selectByMobile(mobile);
    }

    public List<AvuePermission> getAvuePermission(List<Integer> avueRoleIds) {
        List<AvueRole> roles =  avueRoleMapper.selectBatchById(avueRoleIds);
        return roles.stream().map(AvueRole::getPermissions).flatMap(Collection::stream).toList();
    }

    public List<AvuePermission> getAvuePermission(String username) {
        EuserDao euserDao = euserMapper.selectByUsername(username);
        return getAvuePermission(euserDao.getAvueRoles());
    }

    private String prepostParam(String selectParam) {
        if (StringUtils.isBlank(selectParam)) {
            selectParam = null;
        } else {
            selectParam = "%" + selectParam + "%";
        }
        return selectParam;
    }
}
