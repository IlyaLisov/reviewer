package com.example.reviewer.repository;

import com.example.reviewer.model.general.Setting;
import com.example.reviewer.model.general.SettingType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SettingRepository extends CrudRepository<Setting, Long> {

    Optional<Setting> findByType(SettingType type);
}
