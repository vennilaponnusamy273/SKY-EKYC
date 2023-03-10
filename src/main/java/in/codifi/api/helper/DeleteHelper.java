package in.codifi.api.helper;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.api.entity.ApplicationUserEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.AccesslogRepository;
import in.codifi.api.repository.AddressRepository;
import in.codifi.api.repository.ApplicationUserRepository;
import in.codifi.api.repository.BankRepository;
import in.codifi.api.repository.CkycResponseRepos;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.repository.GuardianRepository;
import in.codifi.api.repository.NomineeRepository;
import in.codifi.api.repository.PaymentRepository;
import in.codifi.api.repository.PennyDropRepository;
import in.codifi.api.repository.ProfileRepository;
import in.codifi.api.repository.ReqResRepository;
import in.codifi.api.repository.SegmentRepository;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class DeleteHelper {
	@Inject
	ApplicationUserRepository repository;
	@Inject
	AccesslogRepository accesslogRepository;
	@Inject
	AddressRepository addressRepository;
	@Inject
	BankRepository bankRepository;
	@Inject
	CkycResponseRepos ckycResponseRepos;
	@Inject
	DocumentRepository documentRepository;
	@Inject
	GuardianRepository guardianRepository;
	@Inject
	NomineeRepository nomineeRepository;
	@Inject
	PaymentRepository paymentRepository;
	@Inject
	PennyDropRepository pennyDropRepository;
	@Inject
	ProfileRepository profileRepository;
	@Inject
	ReqResRepository reqResRepository;
	@Inject
	SegmentRepository segmentRepository;

	/**
	 * Method to star over the application
	 * 
	 * @author prade
	 * @param applicationUserEntity
	 * @return
	 */
	public ResponseModel DeleteAll(ApplicationUserEntity applicationUserEntity) {
		ResponseModel responseModel = new ResponseModel();
		applicationUserEntity.setPanNumber(null);
		applicationUserEntity.setFirstName(null);
		applicationUserEntity.setLastName(null);
		applicationUserEntity.setDob(null);
		applicationUserEntity.setCreatedBy(null);
		applicationUserEntity.setCreatedOn(null);
		applicationUserEntity.setUpdatedBy(null);
		applicationUserEntity.setMiddleName(null);
		applicationUserEntity.setStage(1);
		applicationUserEntity.setStatus(EkycConstants.EKYC_STATUS_INPROGRESS);
		applicationUserEntity.setUserName(null);
		applicationUserEntity.setUpdatedOn(new Date());
		repository.save(applicationUserEntity);
		accesslogRepository.deleteByApplicationId(applicationUserEntity.getId().toString());
		addressRepository.deleteByApplicationId(applicationUserEntity.getId());
		bankRepository.deleteByApplicationId(applicationUserEntity.getId());
		ckycResponseRepos.deleteByApplicationId(applicationUserEntity.getId());
		documentRepository.deleteByApplicationId(applicationUserEntity.getId());
		guardianRepository.deleteByApplicationId(applicationUserEntity.getId());
		nomineeRepository.deleteByApplicationId(applicationUserEntity.getId());
		paymentRepository.deleteByApplicationId(applicationUserEntity.getId());
		pennyDropRepository.deleteByApplicationId(applicationUserEntity.getId());
		profileRepository.deleteByApplicationId(applicationUserEntity.getId());
		reqResRepository.deleteByApplicationId(applicationUserEntity.getId());
		segmentRepository.deleteByApplicationId(applicationUserEntity.getId());
		responseModel.setMessage(EkycConstants.SUCCESS_MSG);
		responseModel.setStat(EkycConstants.SUCCESS_STATUS);
		responseModel.setPage(EkycConstants.PAGE_PAN);
		responseModel.setResult("User Details deleted SuccessFully");
		return responseModel;
	}
}
