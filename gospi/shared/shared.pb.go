// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.26.0
// 	protoc        v3.19.4
// source: shared/shared.proto

package shared

import (
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

type Empty struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields
}

func (x *Empty) Reset() {
	*x = Empty{}
	if protoimpl.UnsafeEnabled {
		mi := &file_shared_shared_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Empty) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Empty) ProtoMessage() {}

func (x *Empty) ProtoReflect() protoreflect.Message {
	mi := &file_shared_shared_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Empty.ProtoReflect.Descriptor instead.
func (*Empty) Descriptor() ([]byte, []int) {
	return file_shared_shared_proto_rawDescGZIP(), []int{0}
}

type NullableString struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Types that are assignable to Option:
	//	*NullableString_Value
	//	*NullableString_NoValue
	Option isNullableString_Option `protobuf_oneof:"option"`
}

func (x *NullableString) Reset() {
	*x = NullableString{}
	if protoimpl.UnsafeEnabled {
		mi := &file_shared_shared_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *NullableString) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*NullableString) ProtoMessage() {}

func (x *NullableString) ProtoReflect() protoreflect.Message {
	mi := &file_shared_shared_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use NullableString.ProtoReflect.Descriptor instead.
func (*NullableString) Descriptor() ([]byte, []int) {
	return file_shared_shared_proto_rawDescGZIP(), []int{1}
}

func (m *NullableString) GetOption() isNullableString_Option {
	if m != nil {
		return m.Option
	}
	return nil
}

func (x *NullableString) GetValue() string {
	if x, ok := x.GetOption().(*NullableString_Value); ok {
		return x.Value
	}
	return ""
}

func (x *NullableString) GetNoValue() *Empty {
	if x, ok := x.GetOption().(*NullableString_NoValue); ok {
		return x.NoValue
	}
	return nil
}

type isNullableString_Option interface {
	isNullableString_Option()
}

type NullableString_Value struct {
	Value string `protobuf:"bytes,1,opt,name=value,proto3,oneof"`
}

type NullableString_NoValue struct {
	NoValue *Empty `protobuf:"bytes,2,opt,name=noValue,proto3,oneof"`
}

func (*NullableString_Value) isNullableString_Option() {}

func (*NullableString_NoValue) isNullableString_Option() {}

var File_shared_shared_proto protoreflect.FileDescriptor

var file_shared_shared_proto_rawDesc = []byte{
	0x0a, 0x13, 0x73, 0x68, 0x61, 0x72, 0x65, 0x64, 0x2f, 0x73, 0x68, 0x61, 0x72, 0x65, 0x64, 0x2e,
	0x70, 0x72, 0x6f, 0x74, 0x6f, 0x12, 0x0c, 0x67, 0x6f, 0x73, 0x70, 0x69, 0x2e, 0x73, 0x68, 0x61,
	0x72, 0x65, 0x64, 0x22, 0x07, 0x0a, 0x05, 0x45, 0x6d, 0x70, 0x74, 0x79, 0x22, 0x63, 0x0a, 0x0e,
	0x4e, 0x75, 0x6c, 0x6c, 0x61, 0x62, 0x6c, 0x65, 0x53, 0x74, 0x72, 0x69, 0x6e, 0x67, 0x12, 0x16,
	0x0a, 0x05, 0x76, 0x61, 0x6c, 0x75, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x48, 0x00, 0x52,
	0x05, 0x76, 0x61, 0x6c, 0x75, 0x65, 0x12, 0x2f, 0x0a, 0x07, 0x6e, 0x6f, 0x56, 0x61, 0x6c, 0x75,
	0x65, 0x18, 0x02, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x13, 0x2e, 0x67, 0x6f, 0x73, 0x70, 0x69, 0x2e,
	0x73, 0x68, 0x61, 0x72, 0x65, 0x64, 0x2e, 0x45, 0x6d, 0x70, 0x74, 0x79, 0x48, 0x00, 0x52, 0x07,
	0x6e, 0x6f, 0x56, 0x61, 0x6c, 0x75, 0x65, 0x42, 0x08, 0x0a, 0x06, 0x6f, 0x70, 0x74, 0x69, 0x6f,
	0x6e, 0x42, 0x5a, 0x0a, 0x22, 0x6b, 0x65, 0x79, 0x63, 0x6c, 0x6f, 0x61, 0x6b, 0x2e, 0x70, 0x72,
	0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2e, 0x73, 0x70, 0x69, 0x2e, 0x73, 0x68, 0x61, 0x72, 0x65,
	0x64, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x5a, 0x34, 0x67, 0x69, 0x74, 0x68, 0x75, 0x62, 0x2e,
	0x63, 0x6f, 0x6d, 0x2f, 0x72, 0x61, 0x64, 0x65, 0x6b, 0x67, 0x2f, 0x6b, 0x65, 0x79, 0x63, 0x6c,
	0x6f, 0x61, 0x6b, 0x2d, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2d, 0x73, 0x70, 0x69,
	0x2f, 0x67, 0x6f, 0x73, 0x70, 0x69, 0x2f, 0x73, 0x68, 0x61, 0x72, 0x65, 0x64, 0x62, 0x06, 0x70,
	0x72, 0x6f, 0x74, 0x6f, 0x33,
}

var (
	file_shared_shared_proto_rawDescOnce sync.Once
	file_shared_shared_proto_rawDescData = file_shared_shared_proto_rawDesc
)

func file_shared_shared_proto_rawDescGZIP() []byte {
	file_shared_shared_proto_rawDescOnce.Do(func() {
		file_shared_shared_proto_rawDescData = protoimpl.X.CompressGZIP(file_shared_shared_proto_rawDescData)
	})
	return file_shared_shared_proto_rawDescData
}

var file_shared_shared_proto_msgTypes = make([]protoimpl.MessageInfo, 2)
var file_shared_shared_proto_goTypes = []interface{}{
	(*Empty)(nil),          // 0: gospi.shared.Empty
	(*NullableString)(nil), // 1: gospi.shared.NullableString
}
var file_shared_shared_proto_depIdxs = []int32{
	0, // 0: gospi.shared.NullableString.noValue:type_name -> gospi.shared.Empty
	1, // [1:1] is the sub-list for method output_type
	1, // [1:1] is the sub-list for method input_type
	1, // [1:1] is the sub-list for extension type_name
	1, // [1:1] is the sub-list for extension extendee
	0, // [0:1] is the sub-list for field type_name
}

func init() { file_shared_shared_proto_init() }
func file_shared_shared_proto_init() {
	if File_shared_shared_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_shared_shared_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Empty); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_shared_shared_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*NullableString); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	file_shared_shared_proto_msgTypes[1].OneofWrappers = []interface{}{
		(*NullableString_Value)(nil),
		(*NullableString_NoValue)(nil),
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_shared_shared_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   2,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_shared_shared_proto_goTypes,
		DependencyIndexes: file_shared_shared_proto_depIdxs,
		MessageInfos:      file_shared_shared_proto_msgTypes,
	}.Build()
	File_shared_shared_proto = out.File
	file_shared_shared_proto_rawDesc = nil
	file_shared_shared_proto_goTypes = nil
	file_shared_shared_proto_depIdxs = nil
}
