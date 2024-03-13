package com.example.w2mExample.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.w2mExample.entity.ShipEntity;

public interface ShipRepository extends JpaRepository<ShipEntity, Integer> {
	List<ShipEntity> findByNameContainingIgnoreCase(String name);
	
}
