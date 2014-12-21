package com.erpy.dao;

import java.util.List;

public interface SeedMapper {
	public Seed getSeedById(Integer seedId);
	public void insertSeed(Seed seed);
	public void updateSeed(Seed seed);
	public void deleteSeed(Integer seedId);
	public List<Seed> getAllSeeds();
}
