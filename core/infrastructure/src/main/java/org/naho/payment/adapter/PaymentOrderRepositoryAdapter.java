package org.naho.payment.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.payment.command.PaymentOrderQueryCommand;
import org.naho.payment.entity.PaymentOrderEntity;
import org.naho.payment.mapper.PaymentOrderEntityMapper;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.repository.PaymentOrderJpaRepository;
import org.naho.payment.specification.PaymentOrderSpecification;
import org.naho.subscription.entity.SubscriptionPlanEntity;
import org.naho.subscription.repository.SubscriptionPlanJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentOrderRepositoryAdapter implements PaymentOrderRepositoryPort {

    private final PaymentOrderJpaRepository orderJpaRepository;
    private final SubscriptionPlanJpaRepository planJpaRepository;
    private final PaymentOrderEntityMapper orderEntityMapper;

    @Override
    public PaymentOrder save(PaymentOrder paymentOrder) {
        PaymentOrderEntity entity = orderEntityMapper.domainToEntity(paymentOrder);

        SubscriptionPlanEntity planEntity = planJpaRepository.getReferenceById(paymentOrder.getSubscriptionPlanId());
        entity.setSubscriptionPlan(planEntity);

        PaymentOrderEntity savedEntity = orderJpaRepository.save(entity);
        return orderEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<PaymentOrder> findById(Long id) {
        return orderJpaRepository.findById(id)
                .map(orderEntityMapper::entityToDomain);
    }

    @Override
    public Optional<PaymentOrder> findByOrderCode(String orderCode) {
        return orderJpaRepository.findByOrderCode(orderCode)
                .map(orderEntityMapper::entityToDomain);
    }

    @Override
    public Optional<PaymentOrder> findByOrderCodeForUpdate(String orderCode) {
        return orderJpaRepository.findByOrderCodeForUpdate(orderCode)
                .map(orderEntityMapper::entityToDomain);
    }

    @Override
    public boolean existsByOrderCode(String orderCode) {
        return orderJpaRepository.existsByOrderCode(orderCode);
    }

    @Override
    public Optional<PaymentOrder> findPendingByUserId(Long userId) {
        List<PaymentOrderEntity> list = orderJpaRepository.findAllPendingByUserId(userId);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(orderEntityMapper.entityToDomain(list.get(0)));
    }

    @Override
    public List<PaymentOrder> findAllByUserId(Long userId) {
        return orderJpaRepository.findByUserIdOrderByCreatedTimeDesc(userId)
                .stream()
                .map(orderEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public PageData<PaymentOrder> findAllPaymentOrders(PaymentOrderQueryCommand command) {
        Pageable pageable = PageRequest.of(
                command.page() != null ? command.page() : 0,
                command.size() != null ? command.size() : 20,
                Sort.by(
                        Sort.Direction.valueOf(command.sortDirection() != null ? command.sortDirection().name() : "DESC"),
                        command.sortColumn() != null ? command.sortColumn().getColumnName() : "createdTime"
                )
        );

        Specification<PaymentOrderEntity> searchKeywordSpecification = Specification.anyOf(
                PaymentOrderSpecification.hasOrderCode(command.searchKeyword()),
                PaymentOrderSpecification.hasProviderTransactionId(command.searchKeyword())
        );

        Specification<PaymentOrderEntity> specification = Specification.allOf(
                searchKeywordSpecification,
                PaymentOrderSpecification.hasUserId(command.userId()),
                PaymentOrderSpecification.hasStatus(command.status()),
                PaymentOrderSpecification.hasProvider(command.provider()),
                PaymentOrderSpecification.createdTimeBetween(command.createdTimeFrom(), command.createdTimeTo())
        );

        Page<PaymentOrderEntity> page = orderJpaRepository.findAll(specification, pageable);

        return PageData.<PaymentOrder>builder()
                .pageMeta(PageMeta.builder()
                        .currentPage(page.getNumber())
                        .pageSize(page.getSize())
                        .totalPages(page.getTotalPages())
                        .totalElements(page.getTotalElements())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .data(page.getContent()
                        .stream()
                        .map(orderEntityMapper::entityToDomain)
                        .toList()
                )
                .build();
    }

    @Override
    public void expirePendingBefore(Instant now) {
        orderJpaRepository.expirePendingBefore(now);
    }
}
