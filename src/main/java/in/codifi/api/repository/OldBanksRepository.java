package in.codifi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.api.entity.OldBanksEntity;

public interface OldBanksRepository extends JpaRepository<OldBanksEntity, Long> {

	OldBanksEntity findByBankCode(String trim);

}
