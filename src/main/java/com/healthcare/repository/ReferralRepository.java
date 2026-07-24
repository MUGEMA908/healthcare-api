package com.healthcare.repository;

import com.healthcare.entity.Patient;
import com.healthcare.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByPatient(Patient patient);
    List<Referral> findByStatus(Referral.ReferralStatus status);
}
