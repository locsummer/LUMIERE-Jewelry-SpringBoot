package com.agile.jewelryshop.repository;

import com.agile.jewelryshop.model.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingRepository extends JpaRepository<AppSetting, String> {
}
