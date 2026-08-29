package com.marketplace.customer.service;

import com.marketplace.customer.domain.Customer;
import com.marketplace.customer.domain.CustomerAddress;
import com.marketplace.customer.dto.*;
import com.marketplace.customer.repository.CustomerAddressRepository;
import com.marketplace.customer.repository.CustomerRepository;
import com.marketplace.identity.domain.User;
import com.marketplace.identity.repository.UserRepository;
import com.marketplace.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public Customer getOrCreateCustomer(UUID userId) {
        return customerRepository.findById(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            Customer customer = Customer.builder()
                    .user(user)
                    .build();
            return customerRepository.save(customer);
        });
    }

    @Transactional(readOnly = true)
    public CustomerProfileDto getProfile(UUID customerId) {
        Customer customer = getOrCreateCustomer(customerId);
        User user = customer.getUser();
        List<CustomerAddress> addresses = addressRepository.findByCustomerId(customerId);

        return CustomerProfileDto.builder()
                .id(customer.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .currencyPreference(customer.getCurrencyPreference())
                .localePreference(customer.getLocalePreference())
                .marketingOptIn(customer.isMarketingOptIn())
                .addresses(addresses.stream().map(this::toAddressDto).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public CustomerProfileDto updateProfile(UUID customerId, UpdateProfileRequest request) {
        Customer customer = getOrCreateCustomer(customerId);
        User user = customer.getUser();

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);

        if (request.getCurrencyPreference() != null) customer.setCurrencyPreference(request.getCurrencyPreference());
        if (request.getLocalePreference() != null) customer.setLocalePreference(request.getLocalePreference());
        if (request.getMarketingOptIn() != null) customer.setMarketingOptIn(request.getMarketingOptIn());
        customerRepository.save(customer);

        return getProfile(customerId);
    }

    @Transactional
    public AddressDto addAddress(UUID customerId, CreateAddressRequest request) {
        Customer customer = getOrCreateCustomer(customerId);

        if (request.isDefaultShipping()) {
            addressRepository.clearDefaultShipping(customer);
        }
        if (request.isDefaultBilling()) {
            addressRepository.clearDefaultBilling(customer);
        }

        CustomerAddress address = CustomerAddress.builder()
                .customer(customer)
                .addressTitle(request.getAddressTitle() != null ? request.getAddressTitle() : "Home")
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .streetLine1(request.getStreetLine1())
                .streetLine2(request.getStreetLine2())
                .city(request.getCity())
                .stateProvince(request.getStateProvince())
                .postalCode(request.getPostalCode())
                .countryCode(request.getCountryCode())
                .defaultShipping(request.isDefaultShipping())
                .defaultBilling(request.isDefaultBilling())
                .build();

        CustomerAddress saved = addressRepository.save(address);
        return toAddressDto(saved);
    }

    @Transactional
    public void deleteAddress(UUID customerId, UUID addressId) {
        CustomerAddress address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("CustomerAddress", "id", addressId));
        addressRepository.delete(address);
    }

    private AddressDto toAddressDto(CustomerAddress a) {
        return AddressDto.builder()
                .id(a.getId())
                .addressTitle(a.getAddressTitle())
                .recipientName(a.getRecipientName())
                .phoneNumber(a.getPhoneNumber())
                .streetLine1(a.getStreetLine1())
                .streetLine2(a.getStreetLine2())
                .city(a.getCity())
                .stateProvince(a.getStateProvince())
                .postalCode(a.getPostalCode())
                .countryCode(a.getCountryCode())
                .defaultShipping(a.isDefaultShipping())
                .defaultBilling(a.isDefaultBilling())
                .build();
    }
}
