package my.benzol45.bookservice.service


import my.benzol45.bookservice.domain.CheckedOutBook
import my.benzol45.bookservice.mapper.BookMapper
import my.benzol45.bookservice.mapper.MemberMapper
import my.benzol45.bookservice.model.request.RegisterMemberDto
import my.benzol45.bookservice.model.response.BookDto
import my.benzol45.bookservice.model.response.MemberDto
import my.benzol45.bookservice.repository.MemberRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MemberService (
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper,
    private val bookMapper: BookMapper
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

    fun getMemberBooks(id: Long): List<BookDto> =
        (memberRepository.findById(id).orElse(null)
            ?.checkedOutBooks ?: emptyList<CheckedOutBook>())
            .asSequence()
            .map { it.book }
            .map(bookMapper::bookToBookDTO).toList()

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