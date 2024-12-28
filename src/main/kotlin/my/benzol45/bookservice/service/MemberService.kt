package my.benzol45.bookservice.service

import lombok.RequiredArgsConstructor
import lombok.extern.log4j.Log4j
import my.benzol45.bookservice.mapper.MemberMapper
import my.benzol45.bookservice.model.request.RegisterMemberDto
import my.benzol45.bookservice.model.response.MemberDto
import my.benzol45.bookservice.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
@Log4j
@RequiredArgsConstructor
class MemberService (
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper
) {
    fun getAllMembers(pageable: Pageable): Page<MemberDto> =
        memberRepository.findAll(pageable)
            .map(memberMapper::memberToMemberDTO)

    fun getFilteredMembers(surname: String?, email: String?, phone: String?): List<MemberDto> =
        memberRepository.findAllBySurnameAndEmailAndPhone(surname, email, phone)
            .map(memberMapper::memberToMemberDTO)

    fun getMember(id: Long): MemberDto? =
        memberRepository.findById(id).orElse(null)
            ?.let(memberMapper::memberToMemberDTO)

    fun createMember(registerMemberDto: RegisterMemberDto): MemberDto =
        memberMapper.registerMemberDTOToMember(registerMemberDto)
            .let(memberRepository::save)
            .let (memberMapper::memberToMemberDTO)

    fun processBlockMember(id: Long, block: Boolean): MemberDto? =
        memberRepository.findById(id).orElse(null)
            ?.apply {
                this.block = block
                memberRepository.save(this)}
            ?.let(memberMapper::memberToMemberDTO)
}