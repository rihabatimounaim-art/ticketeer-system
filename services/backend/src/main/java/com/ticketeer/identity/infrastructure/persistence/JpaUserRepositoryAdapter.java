package com.ticketeer.identity.infrastructure.persistence;

import com.ticketeer.identity.application.port.UserRepository;
import com.ticketeer.identity.domain.model.User;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.identity.domain.model.UserRole;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    public JpaUserRepositoryAdapter(final SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return springDataUserRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findById(final UserId id) {
        return springDataUserRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    private User toDomain(final UserEntity entity) {
        return new User(
                new UserId(entity.getId()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                UserRole.valueOf(entity.getRole()),
                entity.isActive()
        );
    }
}
